package com.safetynet.alerts.service;

import java.util.List;

public class DataValidatorService {

    public static boolean checkIfExists(Object inputResource, List<?> existingResources) {
        return existingResources.contains(inputResource);
    }
}
