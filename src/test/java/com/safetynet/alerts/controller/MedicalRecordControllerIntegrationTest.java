package com.safetynet.alerts.controller;

import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @AfterEach
    public void restoreOriginalData() throws IOException {
        Files.copy(Paths.get("./src/main/resources/originalData.json"),
                Paths.get("./src/main/resources/data.json"),
                StandardCopyOption.REPLACE_EXISTING);

        medicalRecordRepository.createListMedicalRecords();
    }

    @Test
    public void getMedicalRecords_shouldReturnAllMedicalRecords() throws Exception {
        mockMvc.perform(get("/medicalrecords"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(23))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    public void postMedicalRecord_shouldReturnCreated_andLocationHeader() throws Exception {
        String newMedicalRecord = """
        {
            "firstName": "Rachel",
            "lastName": "Lynn",
            "birthdate": "1973-03-06",
            "medications": ["aspirin:100mg"],
            "allergies": ["peanuts"]
        }
    """;

        mockMvc.perform(post("/medicalrecord")
                .contentType("application/json")
                .content(newMedicalRecord))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    public void updateMedicalRecord_shouldReturnUpdatedMedicalRecord() throws Exception {
        String updatedMedicalRecord = """
        {
            "firstName": "John",
            "lastName": "Boyd",
            "birthdate": "03/06/1984",
            "medications": ["ibuprofen:200mg"],
            "allergies": ["shellfish"]
        }
    """;

        mockMvc.perform(put("/medicalrecord")
                        .contentType("application/json")
                        .content(updatedMedicalRecord))
                .andExpect(status().isOk()) // Vérifie que le statut est 200 OK
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Boyd")))
                .andExpect(jsonPath("$.medications[0]", is("ibuprofen:200mg")))
                .andExpect(jsonPath("$.allergies[0]", is("shellfish")));
    }

    @Test
    public void deleteMedicalRecord_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/medicalrecord")
                        .param("first_name", "John")
                        .param("last_name", "Boyd"))
                .andExpect(status().isOk());
    }
}
