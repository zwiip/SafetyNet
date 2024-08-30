package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@WebMvcTest(controllers = MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private MedicalRecordService medicalRecordServiceMock;


    @Test
    public void testGetMedicalRecords_shouldReturnJsonArray() throws Exception {
        // Arrange
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        medicalRecords.add(new MedicalRecord("Anne", "Shirley", "01/01/2011", new ArrayList<>(List.of("")), new ArrayList<>(List.of(""))));
        medicalRecords.add(new MedicalRecord("Marrila", "Cuthbert", "01/01/1960", new ArrayList<>(List.of("eyedrops:2drops")), new ArrayList<>(List.of(""))));
        medicalRecords.add(new MedicalRecord("Matthew", "Cuthbert", "01/01/1955", new ArrayList<>(List.of("heartpills:100mg")), new ArrayList<>(List.of(""))));

        doReturn(medicalRecords).when(medicalRecordServiceMock).getMedicalRecords();

        // Act & Assert
        mockMvc.perform(get("/medicalrecords"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json("[" +
                        "{\"firstName\":\"Anne\",\"lastName\":\"Shirley\",\"birthdate\":\"01/01/2011\",\"medications\":[],\"allergies\":[]}," +
                        "{\"firstName\":\"Marrila\",\"lastName\":\"Cuthbert\",\"birthdate\":\"01/01/1960\",\"medications\":[\"eyedrops:2drops\"],\"allergies\":[]}," +
                        "{\"firstName\":\"Matthew\",\"lastName\":\"Cuthbert\",\"birthdate\":\"01/01/1955\",\"medications\":[\"heartpills:100mg\"],\"allergies\":[]}" +
                        "]"));
    }
}
