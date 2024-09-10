package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MedicalRecordService medicalRecordServiceMock;

    @Test
    public void getMedicalRecords_shouldReturnJsonArray() throws Exception {
        // Arrange
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        medicalRecords.add(new MedicalRecord("Anne", "Shirley", "01/01/2011", new ArrayList<>(List.of("")), new ArrayList<>(List.of(""))));
        medicalRecords.add(new MedicalRecord("Marilla", "Cuthbert", "01/01/1960", new ArrayList<>(List.of("eyedrops:2drops")), new ArrayList<>(List.of(""))));
        medicalRecords.add(new MedicalRecord("Matthew", "Cuthbert", "01/01/1955", new ArrayList<>(List.of("heartpills:100mg")), new ArrayList<>(List.of(""))));

        doReturn(medicalRecords).when(medicalRecordServiceMock).getMedicalRecords();

        // Act & Assert
        mockMvc.perform(get("/medicalrecords"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(medicalRecords.size()))
                .andExpect(jsonPath("$[0].firstName").value("Anne"));
    }

    @Test
    public void addMedicalRecord_shouldHaveStatusOK_andReturnTheMedicalRecordCreated() throws Exception {
        // Arrange
        MedicalRecord medicalRecordToAdd = new MedicalRecord("Felicia", "Boyd", "01/01/2000", new ArrayList<>(List.of("tetracyclaz:650mg")),new ArrayList<>(List.of("xilliathal")));

        doReturn(medicalRecordToAdd).when(medicalRecordServiceMock).createMedicalRecord(medicalRecordToAdd);

        // Act & Assert
        mockMvc.perform(post("/medicalrecord"))
                .andExpect(status().isOk());


    }
}
