package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.exceptions.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class MedicalRecordRepository {

    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordRepository.class);
    List<MedicalRecord> medicalRecords;
    private final DataRepository dataRepository;

    /* CONSTRUCTOR */
    public MedicalRecordRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        createListMedicalRecords();
    }

    /* METHODS */

    /**
     * Take a JsonNode object and fetch the values for the key "medicalrecords"  in order to create a list of Medical Records
     *
     * @throws RuntimeException if an error occurs while creating the list
     */
    public void createListMedicalRecords() {
        try {
            logger.debug("Creating medical records list from JSON file");
            JsonNode data = dataRepository.getData();
            JsonNode medicalRecordNode = data.get("medicalrecords");
            TypeReference<List<MedicalRecord>> typeReferenceList = new TypeReference<>() {};
            this.medicalRecords = new ObjectMapper().readValue(medicalRecordNode.traverse(), typeReferenceList);
            logger.info("Medical records list created successfully");
        } catch (IOException e) {
            throw new RuntimeException("Error while creating MedicalRecords list", e);
        }
    }

    /**
     * Retrieves the list of all medical records.
     *
     * @return the list of medical records
     */
    public List<MedicalRecord> findAll() {
        logger.debug("Finding all medical records");
        return medicalRecords;
    }

    /**
     * Browse through the medical records to find the one matching with the given first name and last name
     *
     * @param firstName a string representing the first name of the person we are looking for
     * @param lastName a string representing the last name of the person we are looking for
     * @return the medical record or null if not found
     */
    public MedicalRecord findMedicalRecordsByFullName(String firstName, String lastName) {
        logger.debug("Finding medical record for {} {}", firstName, lastName);
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)) {
                logger.debug("Found medical record for {} {}", firstName, lastName);
                return medicalRecord;
            }
        }
        logger.warn("No medical record found for {} {}", firstName, lastName);
        return null;
    }

    /**
     * Add a new Medical Record to the list and update the JSON file.
     *
     * @param medicalRecord a new medical record to add
     * @return the added medical record
     */
    public MedicalRecord save(MedicalRecord medicalRecord) {
        logger.debug("Saving new medical record for {} {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
        medicalRecords.add(medicalRecord);
        updateMedicalRecordsList(medicalRecords);
        logger.info("Medical record saved successfully for {} {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
        return medicalRecord;
    }

    /**
     * Delete the medical record matching the given firstname and lastname.
     *
     * @param firstName a string representing the first name of the person we are looking for.
     * @param lastName a string representing the last name of the person we are looking for.
     * @throws ResourceNotFoundException if the medical record isn't found.
     */
    public boolean delete(String firstName, String lastName) {
        logger.debug("Deleting medical record for {} {}", firstName, lastName);
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equals(firstName) &&
               medicalRecord.getLastName().equals(lastName)) {
                medicalRecords.remove(medicalRecord);
                updateMedicalRecordsList(medicalRecords);
                logger.info("Medical record deleted successfully for {} {}", firstName, lastName);
                return true;
            }
        }
        throw new ResourceNotFoundException("It seems there is no Medical Record for: " + firstName + " " + lastName);
    }

    /**
     * Update an existing medical record with the new data and update the JSON file
     *
     * @param inputMedicalRecord a medical record with updated data
     * @return the updated medical record
     * @throws ResourceNotFoundException if the medical record isn't found.
     */
    public MedicalRecord update(MedicalRecord inputMedicalRecord) throws ResourceNotFoundException {
        logger.debug("Updating medical record for {} {}", inputMedicalRecord.getFirstName(), inputMedicalRecord.getLastName());
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equals(inputMedicalRecord.getFirstName()) &&
               medicalRecord.getLastName().equals(inputMedicalRecord.getLastName())) {
                medicalRecords.set(medicalRecords.indexOf(medicalRecord), inputMedicalRecord);
                updateMedicalRecordsList(medicalRecords);
                logger.info("Medical record updated successfully for {} {}", inputMedicalRecord.getFirstName(), inputMedicalRecord.getLastName());
                return inputMedicalRecord;
            }
        }
        throw new ResourceNotFoundException("It seems there is no Medical Record for: " + inputMedicalRecord.getFirstName() + " " + inputMedicalRecord.getLastName());
    }

    /**
     * Turn the medical records list into a JsonNode object in order to write it int the JSON file as value for the key "medicalrecords"
     *
     * @param medicalRecords the list of medical records with the new data to write to the JSON file
     */
    public void updateMedicalRecordsList(List<MedicalRecord> medicalRecords) {
        logger.debug("Updating medical records list");
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        rootNode.set("medicalrecords", objectMapper.valueToTree(medicalRecords));
        dataRepository.writeData(rootNode);
        logger.info("Medical records updated successfully, now {} entries", medicalRecords.size());
    }
}
