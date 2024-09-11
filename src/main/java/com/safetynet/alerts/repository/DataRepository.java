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

    /* VARIABLES */
    Logger logger = LoggerFactory.getLogger(DataRepository.class);
    public ObjectMapper objectMapper = new ObjectMapper();
    private final File file;

    /* CONSTRUCTORS */
    /**
     * Default constructor, uses the default data file.
     */
    public DataRepository() {
        this.file = new File("./src/main/resources/data.json");
        logger.info("Using default data file: {}", file.getPath());
    }

    /**
     * Constructor with file path for flexibility during testing or evolutions
     * @param path the path to the JSON file.
     */
    public DataRepository(String path) {
        this.file = new File(path);
        logger.info("Using custom data file: {}", file.getPath());
    }

    /* METHODS */

    /**
     * Read the JSON file and turn the content in a JsonNode object in order to exploit it
     *
     * @return JsonNode of the Data from the file.
     * @throws RuntimeException if  an I/O error occurs during file reading
     */
    public JsonNode getData() {
        try {
            logger.info("Reading data from file: {}", file.getPath());
            return objectMapper.readTree(file);

        } catch (IOException e) {
            throw new RuntimeException("Error reading data from file: " + file.getPath(), e);
        }
    }

    /**
     * Write the JsonNode with the new data into the JSON file
     *
     * @param data a JsonNode with the updated data
     * @throws RuntimeException if an I/O error occurs during file writing
     */
    public void writeData(JsonNode data) {
        try {
            logger.info("Writing data to file: {}", file.getPath());
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException("Error writing data to file: " + file.getPath(), e);
        }
    }
}
