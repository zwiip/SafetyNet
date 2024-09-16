package com.safetynet.alerts.controller;

import com.safetynet.alerts.repository.DataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class FireStationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUpData() throws Exception {
        Files.copy(Paths.get("./src/main/resources/originalData.json"),
                Paths.get("./src/main/resources/data.json"),
                StandardCopyOption.REPLACE_EXISTING);

        DataRepository dataRepository = new DataRepository();
    }

    @AfterEach
    public void restoreOriginalData() throws IOException {
        Files.copy(Paths.get("./src/main/resources/originalData.json"),
                Paths.get("./src/main/resources/data.json"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void getFireStations_shouldReturnAllFireStations() throws Exception {
        mockMvc.perform(get("/firestations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(13))
                .andExpect(jsonPath("$[0].address", is("1509 Culver St")));
    }

    @Test
    public void getFireStationPersonsList_shouldReturnCoveredPersons() throws Exception {
        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adultsCount").value(5))
                .andExpect(jsonPath("$.childCount").value(1));
    }

    @Test
    public void getPhoneList_shouldReturnPhoneList() throws Exception {
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(4));
    }

    @Test
    public void getPersonsAtThisAddress_shouldReturnPersonsList() throws Exception {
        mockMvc.perform(get("/fire")
                        .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personsAtThisAddress.length()").value(5));
    }

    @Test
    public void getFloodAlert_shouldReturnAddressesAndPersonsCovered() throws Exception {
        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    public void postFireStation_shouldReturnCreated_andLocationHeader() throws Exception {
        String newFireStation = """
            {
                "address": "123 New Street",
                "station": "5"
            }
        """;

        mockMvc.perform(post("/firestation")
                        .contentType("application/json")
                        .content(newFireStation))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    public void updateFireStation_shouldReturnUpdatedFireStation() throws Exception {
        String updatedFireStation = """
            {
                "address": "951 LoneTree Rd",
                "station": "4"
            }
        """;

        mockMvc.perform(put("/firestation")
                        .contentType("application/json")
                        .content(updatedFireStation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station", is("4")));
    }

    @Test
    public void deleteFireStation_shouldReturnStatusOk() throws Exception {
        mockMvc.perform(delete("/firestation")
                        .param("address", "892 Downing Ct"))
                .andExpect(status().isOk());
    }

}
