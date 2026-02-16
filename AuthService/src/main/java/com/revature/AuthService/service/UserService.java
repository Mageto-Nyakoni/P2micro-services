package com.revature.AuthService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.AuthService.client.DoctorClient;
import com.revature.AuthService.client.PatientClient;
import com.revature.AuthService.model.Doctor;
import com.revature.AuthService.model.Patient;
import com.revature.AuthService.model.User;
import com.revature.AuthService.repository.UserRepository;
import com.revature.AuthService.response.UserTableResponse;

import jakarta.transaction.Transactional;

@Service
public class UserService implements ServiceInterface<User> {
    private final UserRepository userRepository;
    private final DoctorClient doctorClient;
    private final PatientClient patientClient;

    public UserService(UserRepository userRepository, DoctorClient doctorClient, PatientClient patientClient) {
        this.userRepository = userRepository;
        this.doctorClient = doctorClient;
        this.patientClient = patientClient;
    }

    @Override
    public User save(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<User> deleteById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {

            Doctor doctor = doctorClient.findByUserId(id);
            if (doctor != null) {
                doctorClient.deleteDoctor(doctor.getDoctorId());
            }

            Patient patient = patientClient.findByUserId(id);
            if (patient != null) {
                patientClient.deletePatient(patient.getPatientId());
            }
            
            userRepository.deleteById(id);
        }

        return optionalUser;
    }

    @Override
    public User updateById(int id, User newUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (newUser.getEmail() != null) user.setEmail(newUser.getEmail());
            if (newUser.getPassword() != null) user.setPassword(newUser.getPassword());
            if (newUser.getFirstName() != null) user.setFirstName(newUser.getFirstName());
            if (newUser.getLastName() != null) user.setLastName(newUser.getLastName());
            if (newUser.getPrivilege() != null) user.setPrivilege(newUser.getPrivilege());
            return userRepository.save(user);
        }
        return null;
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public List<UserTableResponse> getUsersForTable() {
        return userRepository.findUsersForTable();
    }
}
