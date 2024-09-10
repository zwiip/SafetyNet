package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
public class DataRepository {

    /** VARIABLES **/
    Logger logger = LoggerFactory.getLogger(DataRepository.class);
    public ObjectMapper objectMapper = new ObjectMapper();
    private final File file;

    /** CONSTRUCTORS **/
    public DataRepository() {
        this.file = new File("./src/main/resources/data.json");
    }

    public DataRepository(String path) {
        this.file = new File(path);
    }

    /** METHODS **/

    /**
     * Read a file and turn the data in a JsonNode in order to exploit them
     *
     * @return JsonNode of the Data in the file.
     * @throws RuntimeException if something goes wrong with the reading of the file
     */
    public JsonNode getData() {
        try {
            logger.info("Data collected from file");
            return objectMapper.readTree(file);

        } catch (IOException e) {
            logger.error("Something went wrong trying to collect the data from the file" + e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Write the JsonNode with the new data into the JSON file
     *
     * @param data a JsonNode with the updated data
     * @throws RuntimeException
     */
    public void writeData(JsonNode data) {
        try {
            logger.info("File updated with the new datas");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            logger.error("Something went wrong with the writing of the file" + e);
            throw new RuntimeException(e);
        }
    }
}
