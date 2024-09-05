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

    public void delete(String firstName, String lastName) throws IOException {
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equals(firstName) &&
               medicalRecord.getLastName().equals(lastName)) {
                medicalRecords.remove(medicalRecord);
                updateMedicalRecordsList(medicalRecords);
                return;
            }
        }
        throw new IllegalArgumentException("MedicalRecord not found: " + firstName + " " + lastName);
    }

    public MedicalRecord update(MedicalRecord inputMedicalRecord) throws IOException {
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equals(inputMedicalRecord.getFirstName()) &&
               medicalRecord.getLastName().equals(inputMedicalRecord.getLastName())) {
                medicalRecords.set(medicalRecords.indexOf(medicalRecord), inputMedicalRecord);
                updateMedicalRecordsList(medicalRecords);
                return inputMedicalRecord;
            }
        }
        throw new IllegalArgumentException("MedicalRecord not found: " + inputMedicalRecord);
    }

    public void updateMedicalRecordsList(List<MedicalRecord> medicalRecords) throws IOException {
        ObjectNode rootNode = (ObjectNode) dataRepository.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        ((ObjectNode) dataRepository.getData()).set("medicalrecords", objectMapper.valueToTree(medicalRecords));
        dataRepository.writeData(rootNode);
    }
}
