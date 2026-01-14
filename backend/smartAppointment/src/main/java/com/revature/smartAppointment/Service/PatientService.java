package com.revature.smartAppointment.Service;

import com.revature.smartAppointment.Model.Allergy;
import com.revature.smartAppointment.Model.Patient;
import com.revature.smartAppointment.Repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService implements ServiceInterface<Patient> {
    private PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient save(Patient entity) {
        return patientRepository.save(entity);
    }

    @Override
    public Optional<Patient> findById(int id) {
        return patientRepository.findById(id);
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> deleteById(int id) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isPresent()) {
            patientRepository.deleteById(id);
        }
        return optionalPatient;
    }

    @Override
    public Patient updateById(int id, Patient newPatient) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isPresent()) {
            Patient patient = optionalPatient.get();
            if (newPatient.getAge() != null) patient.setAge(newPatient.getAge());
            if (newPatient.getGender() != null) patient.setGender(newPatient.getGender());
            if (newPatient.getPhoneNumber() != null) patient.setPhoneNumber(newPatient.getPhoneNumber());
            if (newPatient.getDateOfBirth() != null) patient.setDateOfBirth(newPatient.getDateOfBirth());
            if (newPatient.getAddress() != null) patient.setAddress(newPatient.getAddress());
            if (newPatient.getBloodType() != null) patient.setBloodType(newPatient.getBloodType());
            if (newPatient.getAllergies() != null) patient.setAllergies(newPatient.getAllergies());

            return patientRepository.save(patient);
        }
        return null;
    }
}
