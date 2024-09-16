package com.safetynet.alerts.service;

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
     * Retrieves the list of all Medical Records.
     * @return a list of MedicalRecord object.
     */
    public List<MedicalRecord> getMedicalRecords() {
        logger.debug("Retrieving all medical records");
        return medicalRecordRepository.findAll();
    }

    /**
     * Retrieve the medical record matching with the given first name and last name.
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @return a MedicalRecord object matching the given first name and last name.
     */
    public MedicalRecord getOneMedicalRecord(String firstName, String lastName) {
        logger.debug("Retrieving medical record for {} {}", firstName, lastName);
        return medicalRecordRepository.findMedicalRecordsByFullName(firstName, lastName);
    }

    /**
     * Calculates the age of a person matching the given first and last name.
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
            logger.info("Calculated age for {} {}: {} years", firstName, lastName, age);
            return age;

        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing birthdate for " + firstName + " "  + lastName, e);
        }
    }

    /**
     * Determines if the person, matching the given inputs, is a child (18 years or younger).
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     * @return true if the person is 18 years old or younger, false otherwise.
     */
    public boolean isChild(String firstName, String lastName) {
        logger.debug("Checking if {} {} is a child", firstName, lastName);
        return getAge(firstName, lastName) <= 18;
    }

    /**
     * Creates a new medical Record.
     * @param medicalRecord the MedicalRecord object to create.
     * @return the created MedicalRecord object.
     */
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        logger.debug("Creating medical record {}", medicalRecord);
        return medicalRecordRepository.save(medicalRecord);
    }

    /**
     * Deletes the medical record matching the inputs.
     * @param firstName a String representing the first name of the person.
     * @param lastName a String representing the last name of the person.
     */
    public void deleteMedicalRecord(String firstName, String lastName) {
        logger.debug("Deleting medical record for {} {}", firstName, lastName);
        medicalRecordRepository.delete(firstName, lastName);
    }

    /**
     * Updates an existing medical record.
     * @param medicalRecord the MedicalRecord object to update.
     * @return the updated MedicalRecord object.
     */
    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        logger.debug("Updating medical record {}", medicalRecord);
        return medicalRecordRepository.update((medicalRecord));
    }
}
