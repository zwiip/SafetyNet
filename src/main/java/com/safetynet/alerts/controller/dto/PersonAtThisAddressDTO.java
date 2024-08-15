package com.safetynet.alerts.controller.dto;

public class PersonAtThisAddressDTO {
    String lastName;
    String phone;
    long age;
    MedicalRecordDTO medicalRecord;

    public PersonAtThisAddressDTO(String lastName, String phone, long age, MedicalRecordDTO medicalRecord) {
        this.lastName = lastName;
        this.phone = phone;
        this.age = age;
        this.medicalRecord = medicalRecord;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public long getAge() {
        return age;
    }

    public MedicalRecordDTO getMedicalRecord() {
        return medicalRecord;
    }
}