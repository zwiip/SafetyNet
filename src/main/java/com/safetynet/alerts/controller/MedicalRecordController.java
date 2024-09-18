package com.safetynet.alerts.controller;

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

    private final MedicalRecordService medicalRecordService;
    Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/medicalrecords")
    public List<MedicalRecord> getMedicalRecords() {
        return medicalRecordService.getMedicalRecords(); }

    @PostMapping(value = "/medicalrecord")
    public ResponseEntity<MedicalRecord> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        MedicalRecord medicalRecordToAdd = medicalRecordService.createMedicalRecord(medicalRecord);
        if (Objects.isNull(medicalRecordToAdd)) {
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstname}/{lastname}")
                .buildAndExpand(medicalRecordToAdd.getFirstName(), medicalRecordToAdd.getLastName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/medicalrecord")
    public void deleteMedicalRecord(@RequestParam String first_name, @RequestParam String last_name) {
        medicalRecordService.deleteMedicalRecord(first_name, last_name);
    }

    @PutMapping(value = "/medicalrecord")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        MedicalRecord medicalRecordToUpdate = medicalRecordService.updateMedicalRecord(medicalRecord);
        if (Objects.isNull(medicalRecordToUpdate)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medicalRecordToUpdate);
    }
}
