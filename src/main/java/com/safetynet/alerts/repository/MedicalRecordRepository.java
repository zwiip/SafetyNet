package com.safetynet.alerts.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class MedicalRecordRepository {
    List<MedicalRecord> medicalRecords;
    private final DataRepository dataRepository;

    public MedicalRecordRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
        createListMedicalRecords();
    }

    public void createListMedicalRecords() {
        try {
            JsonNode data = dataRepository.getData();
            JsonNode medicalRecordNode = data.get("medicalrecords");
            TypeReference<List<MedicalRecord>> typeReferenceList = new TypeReference<List<MedicalRecord>>() {};
            List<MedicalRecord> medicalRecords = new ObjectMapper().readValue(medicalRecordNode.traverse(), typeReferenceList);
            this.medicalRecords = medicalRecords;
        } catch (IOException e) {
            throw new RuntimeException("Error while creatinf MedicalRecords list", e);
        }
    }

    public List<MedicalRecord> findAll() { return medicalRecords; }

    public MedicalRecord findMedicalRecordsByFullName(String firstName, String lastName) {
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)) {
                return medicalRecord;
            }
        }
        return null;
    }

    public MedicalRecord save(MedicalRecord medicalRecord) throws IOException {
        medicalRecords.add(medicalRecord);
        updateMedicalRecordsList(medicalRecords);
        return medicalRecord;
    }

    public void delete(MedicalRecord medicalRecord) throws IOException {
        medicalRecords.remove(medicalRecord);
        updateMedicalRecordsList(medicalRecords);
    }

    public MedicalRecord update(MedicalRecord medicalRecord) throws IOException {
        medicalRecords.set(medicalRecords.indexOf(medicalRecord), medicalRecord);
        updateMedicalRecordsList(medicalRecords);
        return medicalRecord;
    }

    public void updateMedicalRecordsList(List<MedicalRecord> medicalRecords) throws IOException {
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        ((ObjectNode) dataRepository.getData()).set("medicalrecords", objectMapper.valueToTree(medicalRecords));
        dataRepository.writeData(rootNode);
    }
}
