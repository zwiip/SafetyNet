package com.safetynet.alerts.service;

import com.safetynet.alerts.controller.dto.*;
import com.safetynet.alerts.exceptions.ResourceAlreadyExistException;
import com.safetynet.alerts.exceptions.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
     *
     * @return a list of FireStation objects.
     */
    public List<FireStation> getFireStations() {
        logger.debug("retrieving all fire stations");
        List<FireStation> fireStations = fireStationRepository.findAll();
        logger.debug("Retrieved {} fire stations", fireStations.size());
        return fireStations;
    }

    /**
     * Create an object listing all persons whose address is covered by a fire station, grouped by adults and children.
     *
     * @param stationNumber the String of the fire station's number.
     * @return a CoveredPersonsListDTO object containing adults and children counts and person details.
     * @throws ResourceNotFoundException if no addresses are recorded for the given station number.
     */
    public CoveredPersonsListDTO createFireStationPersonsList(String stationNumber) {
        logger.debug("Creating list of persons covered by fire station {}", stationNumber);
        int childCounter = 0;
        int adultsCounter = 0;
        ArrayList<PersonDTO> fireStationPersonsList = new ArrayList<>();
        List<String> coveredAddresses = fireStationRepository.getCoveredAddresses(stationNumber);
        if (coveredAddresses.isEmpty()) {
            throw new ResourceNotFoundException("No addresses recorded for the station number: " + stationNumber);
        }
        for (String address : coveredAddresses) {
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
        logger.debug("Fire station {} covers {} adults and {} children", stationNumber, adultsCounter, childCounter);
        return new CoveredPersonsListDTO(childCounter, adultsCounter, fireStationPersonsList);
    }

    /**
     * Retrieves a list of phone numbers of persons covered by a given fire station.
     *
     * @param firestationNumber the number of the fire station.
     * @return a set of phone numbers to avoid duplicates.
     * @throws ResourceNotFoundException if no addresses are recorded for the given station number.
     */
    public Set<String> createPhoneList(String firestationNumber) {
        logger.debug("Creating phone list for fire station {}, which covers {} addresses", firestationNumber, fireStationRepository.getCoveredAddresses(firestationNumber));
        Set<String> phoneList = new HashSet<>();
        List<String> coveredAddresses = fireStationRepository.getCoveredAddresses(firestationNumber);
        if (coveredAddresses.isEmpty()) {
            throw new ResourceNotFoundException("No addresses recorded for the station number: " + firestationNumber);
        }
        for (String address : coveredAddresses) {
            logger.debug("Processing address: {}", address);
            for (Person person : personService.getPersons())  {
                if (person.getAddress().equals(address)) {
                    phoneList.add(person.getPhone());
                    logger.debug("Adding number of {} : {} ", person.getFirstName(), person.getPhone());
                }
            }
        }
        logger.debug("Fire station {} covers {} phone", firestationNumber, phoneList.size());
        return phoneList;
    }

    /**
     * Creates a list of persons living at a specific address, including their medical records.
     *
     * @param address a String representing the address to check.
     * @return a PersonsListInCaseOfFireDTO object containing station number and persons details.
     * @throws ResourceNotFoundException if no data is found for this address.
     */
    public PersonsListInCaseOfFireDTO createPersonsListInCaseOfFire(String address) {
        logger.debug("Creating list of persons at the address {}", address);
        String stationNumber = fireStationRepository.getStationNumber(address);
        if (stationNumber == null) {
            throw new ResourceNotFoundException("No data for this address: " + address);
        }
        ArrayList<PersonAtThisAddressDTO> personsAtThisAddressList = personService.createPersonsAtThisAddressList(address);
        logger.info("A list of {} persons at the address {} in case of fire, covered by fire station {} has been created", personsAtThisAddressList.size(), address, stationNumber);
        return new PersonsListInCaseOfFireDTO(stationNumber, personsAtThisAddressList);
    }

    /**
     * Creates a list of persons covered by one or multiple fire stations for flood alerts.
     *
     * @param stations a list of String representing station numbers.
     * @return a list of FloodAlertDTO object containing addresses, persons covered and theirs medical records.
     */
    public List<FloodAlertDTO> createFloodAlertList(List<String> stations) {
        logger.debug("Creating flood alert list for stations {}", stations);
        List<FloodAlertDTO> floodAlertList = new ArrayList<>();
        for (String station : stations) {
            for (String address : fireStationRepository.getCoveredAddresses(station)) {
                ArrayList<PersonAtThisAddressDTO> personsAtThisAddressList = personService.createPersonsAtThisAddressList(address);
                floodAlertList.add(new FloodAlertDTO(address, personsAtThisAddressList));
            }
            if (floodAlertList.isEmpty()) {
                logger.warn("No addresses found for the station {}.", station);
            }
        }
        logger.info("Flood alert list created for stations {}, with {} addresses covered", stations, floodAlertList.size());
        return floodAlertList;
    }

    /**
     * Creates a new fire station.
     *
     * @param fireStation the FireStation object to create.
     * @return the created FireStation object.
     * @throws ResourceAlreadyExistException if the firestation address is already in the list
     */
    public FireStation createFireStation(FireStation fireStation) throws ResourceAlreadyExistException {
        logger.debug("adding new fire station {}", fireStation);
        FireStation fireStationToUpdate = fireStationRepository.getFireStationByAddress(fireStation.getAddress());
        if (fireStationToUpdate != null ) {
            throw new ResourceAlreadyExistException("This address already exist with station number " + fireStation.getStation() + ". If you want to change it, please use an update operation.");
        }
        return fireStationRepository.save(fireStation);
    }

    /**
     * Updates a fire station's details.
     *
     * @param fireStation the FireStation object to update.
     * @return the updated FireStation object.
     * @throws ResourceNotFoundException if the given fire station is not found in the firestations list.
     */
    public FireStation updateFireStation(FireStation fireStation) throws ResourceNotFoundException {
        logger.info("Updating fire station at address {}", fireStation.getAddress());
        FireStation fireStationToUpdate = fireStationRepository.getFireStationByAddress(fireStation.getAddress());
        if (fireStationToUpdate == null) {
            throw new ResourceNotFoundException("No fire station for the address: " + fireStation.getAddress());
        }
        return fireStationRepository.update(fireStation);
    }

    /**
     * Deletes a fire station by its address.
     *
     * @param address a string representing the address of the fire station to delete.
     */
    public void deleteFireStation(String address) throws ResourceNotFoundException {
        logger.debug("Deleting fire station at the address: {}", address);
        FireStation fireStationToDelete = fireStationRepository.getFireStationByAddress(address);
        if (fireStationToDelete == null) {
            throw new ResourceNotFoundException("No data in the fire stations list for the address: " + address);
        }
        fireStationRepository.delete(fireStationToDelete.getAddress());
    }

}
