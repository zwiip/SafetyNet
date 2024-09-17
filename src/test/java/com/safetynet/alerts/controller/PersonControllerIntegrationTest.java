package com.safetynet.alerts.controller;

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
public class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        Files.copy(Paths.get("./src/main/resources/originalData.json"),
                Paths.get("./src/main/resources/data.json"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterEach
    public void restoreOriginalData() throws IOException {
        Files.copy(Paths.get("./src/main/resources/originalData.json"),
                Paths.get("./src/main/resources/data.json"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void getPersons_shouldReturnAllPersons() throws Exception {
        mockMvc.perform(get("/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(23)))
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    public void getOnePerson_shouldReturnTheWantedPerson() throws Exception {
        mockMvc.perform(get("/person/Sophia/Zemicks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("soph@email.com")));
    }

    @Test
    public void getChildAlertList_shouldReturnAChildAlertDTOForTheWantedAddress() throws Exception {
        mockMvc.perform(get("/childAlert")
                .param("address", "1509 Culver St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.childList.length()", is(2)))
                .andExpect(jsonPath("$.otherMembersList.length()", is(3)));
    }

    @Test
    public void getPersonInfoLastName_shouldReturnAListOfPersonInfoLastNameDTO() throws Exception {
        mockMvc.perform(get("/personInfolastName")
                        .param("lastName", "Stelzer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].address", is("947 E. Rose Dr")));
    }

    @Test
    public void getCommunityEmail_shouldReturnAListOfEmails() throws Exception {
        mockMvc.perform(get("/communityEmail")
                .param("city", "Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(23)));
    }

    @Test
    public void addOnePerson_shouldCreatedStatusAndTheCreatedPerson() throws Exception {
        String newPerson = """
            {
                "firstName": "Alice",
                "lastName": "Wonderland",
                "address": "123 Wonderland Street",
                "city": "Wonderland",
                "zip": "12345",
                "phone": "123-456-7890",
                "email": "alice@wonderland.com"
            }
        """;

        mockMvc.perform(post("/person")
                .contentType("application/json")
                .content(newPerson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    public void updateOnePerson_shouldUpdateExistingPerson() throws Exception {
        String updatedPersonJson = """
            {
                "firstName": "Sophia",
                "lastName": "Zemicks",
                "address": "New Address",
                "city": "Culver",
                "zip": "97451",
                "phone": "999-999-9999",
                "email": "sophia@newemail.com"
            }
        """;

        mockMvc.perform(put("/person")
                .contentType("application/json")
                .content(updatedPersonJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("New Address")))
                .andExpect(jsonPath("$.email", is("sophia@newemail.com")));
    }

    @Test
    public void deleteOnePerson_shouldDeleteThePerson() throws Exception {
        mockMvc.perform(delete("/person")
                        .param("firstname", "Eric")
                        .param("lastname", "Cadigan"))
                .andExpect(status().isOk());
    }
}
