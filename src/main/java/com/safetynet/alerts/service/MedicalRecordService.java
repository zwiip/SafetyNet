package com.safetynet.alerts.service;

import com.safetynet.alerts.exceptions.ResourceAlreadyExistException;
import com.safetynet.alerts.exceptions.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class MedicalRecordService {

    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);

    private final MedicalRecordRepository medicalRecordRepository;

    /* CONSTRUCTOR */
    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }

    /* METHODS */
    /**
     * Interacts with the repository layer to retrieve the list of all Medical Records.
     *
     * @return a list of MedicalRecord object.
     */
    public List<MedicalRecord> getMedicalRecords() {
        logger.debug("Retrieving all medical records");
        List<MedicalRecord> medicalRecords = medicalRecordRepository.findAll();
        logger.debug("Retrieved {} medical records", medicalRecords.size());
        return medicalRecords;
    }

    /**
     * Interacts with the repository layer to retrieve the medical record matching with the given first name and last name.
     *
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @return a MedicalRecord object matching the given first name and last name.
     * @throws ResourceNotFoundException if no medical record is matching the inputs.
     */
    public MedicalRecord getOneMedicalRecord(String firstName, String lastName) {
        logger.debug("Retrieving medical record for {} {}", firstName, lastName);
        MedicalRecord medicalRecord = medicalRecordRepository.findMedicalRecordsByFullName(firstName, lastName);
        if (medicalRecord == null) {
            throw new ResourceNotFoundException("No medical record found for " + firstName + " " + lastName);
        }
        logger.debug("Retrieved medical record for {} {}", firstName, lastName);
        return medicalRecord;
    }

    /**
     * Helper method to calculate the age of a person matching the given first and last name.
     *
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @return long of the age of the person in years.
     * @throws IllegalArgumentException if the birthdate format is invalid.
     */
    public long getAge(String firstName, String lastName) {
        logger.debug("Getting age for {} {}", firstName, lastName);
        MedicalRecord medicalRecord = medicalRecordRepository.findMedicalRecordsByFullName(firstName, lastName);
        String stringBirthDate = medicalRecord.getBirthdate();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            Date birthday = formatter.parse(stringBirthDate);
            Date today = new Date();
            long diffInMillies = Math.abs(today.getTime() - birthday.getTime());
            long age = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)/365;
            logger.debug("Calculated age for {} {}: {} years", firstName, lastName, age);
            return age;

        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing birthdate for " + firstName + " "  + lastName, e);
        }
    }

    /**
     * Helper method to determine if the person, matching the given inputs, is a child (18 years or younger).
     *
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @return true if the person is 18 years old or younger, false otherwise.
     */
    public boolean isChild(String firstName, String lastName) {
        logger.debug("Checking if {} {} is a child", firstName, lastName);
        return getAge(firstName, lastName) <= 18;
    }

    /**
     * This method interacts with the repository layer to creates a new medical Record.
     * First it checks if the Medical Record already exists.
     *
     * @param inputMedicalRecord the MedicalRecord object to create.
     * @return the created MedicalRecord object.
     * @throws ResourceAlreadyExistException if we find a Medical Record with the same full name than the one to create.
     */
    public MedicalRecord createMedicalRecord(MedicalRecord inputMedicalRecord) throws ResourceAlreadyExistException {
        logger.debug("Creating medical record for {} {}", inputMedicalRecord.getFirstName(), inputMedicalRecord.getLastName());
        String inputMedicalRecordFirstName = inputMedicalRecord.getFirstName();
        String inputMedicalRecordLastName = inputMedicalRecord.getLastName();
        for (MedicalRecord medicalRecord : getMedicalRecords()) {
            if (medicalRecord.getFirstName().equals(inputMedicalRecordFirstName) && medicalRecord.getLastName().equals(inputMedicalRecordLastName)) {
                throw new ResourceAlreadyExistException("This Medical record already exist " + inputMedicalRecordFirstName + " " + inputMedicalRecordLastName);
            }
        }
        return medicalRecordRepository.save(inputMedicalRecord);
    }

    /**
     * Updates an existing medical record.
     *
     * @param inputMedicalRecord the MedicalRecord object to update.
     * @return the updated MedicalRecord object.
     * @throws ResourceNotFoundException if the medical record is not found is the list.
     */
    public MedicalRecord updateMedicalRecord(MedicalRecord inputMedicalRecord) {
        logger.debug("Updating medical record {}", inputMedicalRecord);
        MedicalRecord medicalRecordToUpdate = getOneMedicalRecord(inputMedicalRecord.getFirstName(), inputMedicalRecord.getLastName());
        if (medicalRecordToUpdate == null) {
            throw new ResourceNotFoundException("No medical record found for " + inputMedicalRecord.getFirstName() + " "+ inputMedicalRecord.getLastName());
        }
        return medicalRecordRepository.update(inputMedicalRecord);
    }

    /**
     * Deletes the medical record matching the inputs.
     *
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @throws ResourceNotFoundException if the medical record is not found in the medical record list.
     */
    public void deleteMedicalRecord(String firstName, String lastName) {
        logger.debug("Deleting medical record for {} {}", firstName, lastName);
        MedicalRecord medicalRecordToDelete = getOneMedicalRecord(firstName, lastName);
        if (medicalRecordToDelete == null) {
            throw new ResourceNotFoundException("No medical record for " + firstName + " " + lastName);
        }
        medicalRecordRepository.delete(medicalRecordToDelete);
    }
}
