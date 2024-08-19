package com.safetynet.alerts.service;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public List<MedicalRecord> getMedicalRecords() { return medicalRecordRepository.findAll(); }

    public MedicalRecord getOneMedicalRecord(String firstName, String lastName) {
        return medicalRecordRepository.findMedicalRecordsByFullName(firstName, lastName);
    }

    public long getAge(String firstName, String lastName) {
        MedicalRecord medicalRecord = medicalRecordRepository.findMedicalRecordsByFullName(firstName, lastName);
        String stringBirthDate = medicalRecord.getBirthdate();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
            Date birthday = formatter.parse(stringBirthDate);
            Date today = new Date();
            long diffInMillies = Math.abs(today.getTime() - birthday.getTime());
            long age = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)/365;
            return age;

        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean isChild(String firstName, String lastName) {
        return getAge(firstName, lastName) <= 18;
    }
}
