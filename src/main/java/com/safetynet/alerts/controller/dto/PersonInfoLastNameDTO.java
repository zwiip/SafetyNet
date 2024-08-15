package com.safetynet.alerts.controller.dto;

public class PersonInfoLastNameDTO {
    String lastName;
    String address;
    long age;
    String mail;
    MedicalRecordDTO medicalRecord;

    public PersonInfoLastNameDTO(String lastName, String address, long age, String mail, MedicalRecordDTO medicalRecord) {
        this.lastName = lastName;
        this.address = address;
        this.age = age;
        this.mail = mail;
        this.medicalRecord = medicalRecord;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public long getAge() {
        return age;
    }

    public String getMail() {
        return mail;
    }

    public MedicalRecordDTO getMedicalRecord() {
        return medicalRecord;
    }
}
