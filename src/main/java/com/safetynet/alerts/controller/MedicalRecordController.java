package com.safetynet.alerts.controller;

import com.safetynet.alerts.exceptions.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class MedicalRecordController {

    /* VARIABLES */
    private final MedicalRecordService medicalRecordService;
    Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);

    /* CONSTRUCTOR */
    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    /* METHODS */

    /**
     * This endpoint is used to fetch a list of all medical records.
     *
     * @return a list of MedicalRecord objects.
     */
    @GetMapping("/medicalrecords")
    public List<MedicalRecord> getMedicalRecords() {
        List<MedicalRecord> medicalRecords = medicalRecordService.getMedicalRecords();
        if (medicalRecords.isEmpty()) {
            logger.warn("No medical records found");
        } else {
            logger.info("Successfully found {} medical records.", medicalRecords.size());
        }
        return medicalRecords;
    }

    /**
     * This endpoint adds a medical record to the system.
     * It takes a json of a medical record in the body.
     * Example usage:
     * POST /medicalrecord
     * Body: {
     *     "firstName" : "Jane",
     *     "lastName" : "Eyre",
     *     "birthdate" : "03/06/1984",
     *     "medications" : [ "vitamins" ],
     *     "allergies" : [ "dogs" ]
     *   }
     *
     * @param medicalRecord a json of a medical record in the body of the request
     * @return a Response Entity with the HTTP status:
     *          - 201 CREATED if the medical record has been successfully created,
     *          - 204 NO CONTENT if the medical record could not be added.
     */
    @PostMapping(value = "/medicalrecord")
    public ResponseEntity<MedicalRecord> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        MedicalRecord medicalRecordToAdd = medicalRecordService.createMedicalRecord(medicalRecord);
        if (Objects.isNull(medicalRecordToAdd)) {
            logger.error("Failed to add the medical record for {} {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstname}/{lastname}")
                .buildAndExpand(medicalRecordToAdd.getFirstName(), medicalRecordToAdd.getLastName())
                .toUri();
        logger.info("Successfully added the medical record for {} {}", medicalRecordToAdd.getFirstName(), medicalRecordToAdd.getLastName());
        return ResponseEntity.created(location).build();
    }

    /**
     * Update an existing medicalRecord from the system with new details.
     * Example usage:
     * PUT /medicalrecord
     * Body: Body: {
     *      *     "firstName" : "Jane",
     *      *     "lastName" : "Eyre",
     *      *     "birthdate" : "03/06/1984",
     *      *     "medications" : [ "doliprane:500gr" ],
     *      *     "allergies" : [ "cats" ]
     *      *   }
     *
     * @param medicalRecord a json of a medical record in the body of the request.
     * @return a Response Entity with the updated medical record and the HTTP status:
     *          - 200 OK: if the medical record has been successfully updated,
     *          - 404 NOT FOUND: if the medical record hasn't been updated.
     */
    @PutMapping(value = "/medicalrecord")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord medicalRecordToUpdate = medicalRecordService.updateMedicalRecord(medicalRecord);
            logger.info("Successfully updated the medical record for {} {}.", medicalRecordToUpdate.getFirstName(), medicalRecordToUpdate.getLastName());
            return ResponseEntity.ok(medicalRecordToUpdate);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This endpoint deletes a medical record from the system.
     * Example usage:
     * DELETE /medicalrecord?first_name=Jane&last_name=Eyre
     *
     * @param first_name a String representing the first name for the medical record to delete.
     * @param last_name a String representing the last name for the medical record to delete.
     * @return a Response Entity indicating the HTTP status:
     *          - 200 OK: if the medical record has been deleted,
     *          - 404 NOT FOUND: if the medical record hasn't been found.
     */
    @DeleteMapping(value = "/medicalrecord")
    public ResponseEntity<Void> deleteMedicalRecord(@RequestParam String first_name, @RequestParam String last_name) {
        logger.debug("Received request to delete the Medical Record for {} {}", first_name, last_name);
        try {
            medicalRecordService.deleteMedicalRecord(first_name, last_name);
            logger.info("Successfully deleted the Medical Record for {} {}", first_name, last_name);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
