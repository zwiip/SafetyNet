package com.safetynet.alerts.model;

import lombok.Data;

@Data
public class PersonModel {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String zip;
    private String phone;
    private String email;
}
