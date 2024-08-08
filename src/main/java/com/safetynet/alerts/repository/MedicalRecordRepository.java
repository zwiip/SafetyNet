package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class MedicalRecordRepository {
    List<MedicalRecord> medicalRecords;

    public void createListMedicalRecords(JsonNode jsonNode) throws IOException {
        JsonNode medicalRecordNode = jsonNode.get("medicalrecords");
        TypeReference<List<MedicalRecord>> typeReferenceList = new TypeReference<List<MedicalRecord>>() {};
        List<MedicalRecord> medicalRecords = new ObjectMapper().readValue(medicalRecordNode.traverse(), typeReferenceList);
        this.medicalRecords = medicalRecords;
    }

    public List<MedicalRecord> findAll() { return medicalRecords; }
}
