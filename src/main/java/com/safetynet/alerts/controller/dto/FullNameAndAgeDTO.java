package com.safetynet.alerts.controller.dto;

public class FullNameAndAgeDTO {
    private String firstName;
    private String lastName;
    private long age;

    public FullNameAndAgeDTO(String firstName, String lastName, long age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getAge() {
        return age;
    }
}
