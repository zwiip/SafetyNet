package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<MedicalRecord>> typeReferenceList = new TypeReference<>() {};
            List<MedicalRecord> medicalRecordsData = objectMapper.readValue(medicalRecordNode.traverse(), typeReferenceList);

            this.medicalRecords = validateMedicalRecordsData(medicalRecordsData);
            updateMedicalRecordsList(this.medicalRecords);
            logger.info("Medical records list created successfully with {} medical records", medicalRecords.size());
        } catch (IOException e) {
            throw new RuntimeException("Error while creating MedicalRecords list", e);
        }
    }

    /**
     * Validates the list of Medical Records by removing any duplicate entries.
     * A duplicate is identified when two medical records have the same first and last name.
     * If duplicates are found, they are removed.
     *
     * @param medicalRecords the list of MedicalRecord objects to validate.
     * @return a new list of MedicalRecord objects with duplicates removed.
     */
    public List<MedicalRecord> validateMedicalRecordsData (List<MedicalRecord> medicalRecords) {
        Set<String> uniqueMedicalRecord = new HashSet<>();
        List<MedicalRecord> filteredMedicalRecords = new ArrayList<>();

        for (MedicalRecord medicalRecord : medicalRecords) {
            String fullName = medicalRecord.getFirstName() + " " + medicalRecord.getLastName();
            if (!uniqueMedicalRecord.contains(fullName)) {
                uniqueMedicalRecord.add(fullName);
                filteredMedicalRecords.add(medicalRecord);
            } else {
                logger.warn("Duplicate medical record found and removed: {}", fullName);
            }
        }
        logger.debug("Validation complete. Total medical records after removing duplicates: {}", filteredMedicalRecords.size());
        return filteredMedicalRecords;
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
     * Update an existing medical record with the new data and update the JSON file
     *
     * @param inputMedicalRecord a medical record with updated data
     * @return the updated medical record
     */
    public MedicalRecord update(MedicalRecord inputMedicalRecord) {
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
        return null;
    }

    /**
     * Delete the given medical record and update the JSON file.
     *
     * @param inputMedicalRecord a MedicalRecord object to delete.
     */
    public void delete(MedicalRecord inputMedicalRecord) {
        logger.debug("Deleting medical record for {} {}", inputMedicalRecord.getFirstName(), inputMedicalRecord.getLastName());
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equals(inputMedicalRecord.getFirstName()) &&
                    medicalRecord.getLastName().equals(inputMedicalRecord.getLastName())) {
                medicalRecords.remove(medicalRecord);
                updateMedicalRecordsList(medicalRecords);
                logger.info("Medical record deleted successfully for {} {}", inputMedicalRecord.getFirstName(), inputMedicalRecord.getLastName());
                return;
            }
        }
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
