package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.*;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FireStationService {

    /* VARIABLES */
    private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);

    private final FireStationRepository fireStationRepository;

    private final MedicalRecordService medicalRecordService;

    private final PersonService personService;

    /* CONSTRUCTOR */
    public FireStationService(FireStationRepository fireStationRepository, PersonService personService, MedicalRecordService medicalRecordService) {
        this.fireStationRepository = fireStationRepository;
        this.personService = personService;
        this.medicalRecordService = medicalRecordService;
    }

    /* METHODS */

    /**
     * Retrieves the list of all fire stations.
     * @return a list of FireStation objects.
     */
    public List<FireStation> getFireStations() {
        logger.debug("retrieving all fire stations");
        return fireStationRepository.findAll();
    }

    /**
     * Create an object listing all persons whose address is covered by a fire station, grouped by adults and children.
     * @param stationNumber the String of the fire station's number.
     * @return a CoveredPersonsListDTO object containing adults and children counts and person details.
     */
    public CoveredPersonsListDTO createFireStationPersonsList(String stationNumber) {
        logger.debug("Creating list of persons covered by fire station {}", stationNumber);
        int childCounter = 0;
        int adultsCounter = 0;
        ArrayList<PersonDTO> fireStationPersonsList = new ArrayList<>();
        for (String address : fireStationRepository.getCoveredAddresses(stationNumber)) {
            for (Person person : personService.getPersons())  {
                if (person.getAddress().equals(address)) {
                    if (medicalRecordService.isChild(person.getFirstName(), person.getLastName())) {
                        childCounter++;
                    } else {
                        adultsCounter++;
                    }
                    fireStationPersonsList.add(new PersonDTO(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone()));
                }
            }
        }
        logger.info("Fire station {} covers {} adults and {} children", stationNumber, childCounter, adultsCounter);
        return new CoveredPersonsListDTO(childCounter, adultsCounter, fireStationPersonsList);
    }

    /**
     * Retrieves a list of phone numbers of persons covered by a given fire station.
     * @param firestationNumber the number of the fire station.
     * @return a set of phone numbers to avoid duplicates.
     */
    public Set<String> createPhoneList(String firestationNumber) {
        logger.debug("Creating phone list for fire station {}, which covers {} addresses", firestationNumber, fireStationRepository.getCoveredAddresses(firestationNumber));
        Set<String> phoneList = new HashSet<>();
        for (String address : fireStationRepository.getCoveredAddresses(firestationNumber)) {
            logger.debug("Processing address: {}", address);
            for (Person person : personService.getPersons())  {
                if (person.getAddress().equals(address)) {
                    phoneList.add(person.getPhone());
                    logger.debug("Adding number of {} : {} ", person.getFirstName(), person.getPhone());
                }
            }
        }
        logger.info("Fire station {} covers {} phone", firestationNumber, phoneList.size());
        return phoneList;
    }

    /**
     * Creates a list of persons living at a specific address, including their medical records.
     * @param address a String representing the address to check.
     * @return a PersonsListInCaseOfFireDTO object containing station number and persons details.
     */
    public PersonsListInCaseOfFireDTO createPersonsAtThisAddressList(String address) {
        logger.debug("Creating list of {} persons at the address {}", personService.getPersonsByAddress(address).size(), address);
        String stationNumber = fireStationRepository.getStationNumber(address);
        ArrayList<PersonAtThisAddressDTO> personsAtThisAddressList = new ArrayList<>();
        for ( Person person : personService.getPersonsByAddress(address)) {
            long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
            MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
            MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
            personsAtThisAddressList.add(new PersonAtThisAddressDTO(person.getLastName(), person.getPhone(), age, medicalRecordDTO));
        }
        logger.info("A list of {} persons at the address {}, covered by fire station {} has been created", personsAtThisAddressList.size(), address, stationNumber);
        return new PersonsListInCaseOfFireDTO(stationNumber, personsAtThisAddressList);
    }

    /**
     * Creates a list of persons covered by one or multiple fire stations for flood alerts.
     * @param stations a list of String representing station numbers.
     * @return a list of FloodAlertDTO object containing addresses, persons covered and theirs medical records.
     */
    public List<FloodAlertDTO> createFloodAlertList(List<String> stations) {
        logger.debug("Creating flood alert list for stations {}", stations);
        List<FloodAlertDTO> floodAlertList = new ArrayList<>();
        for (String station : stations) {
            for (String address : fireStationRepository.getCoveredAddresses(station)) {
                ArrayList<PersonAtThisAddressDTO> personsAtThisAddressList = new ArrayList<>();
                for ( Person person : personService.getPersonsByAddress(address)) {
                    long age = medicalRecordService.getAge(person.getFirstName(), person.getLastName());
                    MedicalRecord medicalRecord = medicalRecordService.getOneMedicalRecord(person.getFirstName(), person.getLastName());
                    MedicalRecordDTO medicalRecordDTO = (new MedicalRecordDTO(medicalRecord.getMedications(), medicalRecord.getAllergies()));
                    personsAtThisAddressList.add(new PersonAtThisAddressDTO(person.getLastName(), person.getPhone(), age, medicalRecordDTO));
                }
                floodAlertList.add(new FloodAlertDTO(address, personsAtThisAddressList));
            }

        }
        logger.info("Flood alert list created for stations {}, with {} addresses covered", stations, floodAlertList.size());
        return floodAlertList;
    }

    /**
     * Creates a new fire station.
     * @param fireStation the FireStation object to create.
     * @return the created FireStation object.
     * @throws IOException if an I/O error occurs.
     */
    public FireStation createFireStation(FireStation fireStation) throws IOException {
        logger.debug("adding new fire station {}", fireStation);
        return fireStationRepository.save(fireStation);
    }

    /**
     * Deletes a fire station by its address.
     * @param address a string representing the address of the fire station to delete.
     * @throws IOException if an I/O error occurs.
     */
    public void deleteFireStation(String address) throws IOException {
        logger.debug("Deleting fire station at the address: {}", address);
        fireStationRepository.delete(address);
    }

    /**
     * Updates a fire station's details.
     * @param fireStation the FireStation object to update.
     * @return the updated FireStation object.
     */
    public FireStation updateFireStation(FireStation fireStation) {
        logger.info("Updating fire station at address {}", fireStation.getAddress());
        return fireStationRepository.update(fireStation);
    }
}
