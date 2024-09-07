package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/medicalrecords")
    public List<MedicalRecord> getMedicalRecords() { return medicalRecordService.getMedicalRecords(); }

    @PostMapping(value = "/medicalrecord")
    public ResponseEntity<MedicalRecord> addMedicalRecord(@RequestBody MedicalRecord medicalRecord) throws IOException {
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
}
