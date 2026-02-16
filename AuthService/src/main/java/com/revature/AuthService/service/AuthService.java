package com.revature.AuthService.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.AuthService.client.DoctorClient;
import com.revature.AuthService.client.PatientClient;
import com.revature.AuthService.model.Doctor;
import com.revature.AuthService.model.Patient;
import com.revature.AuthService.model.Privilege;
import com.revature.AuthService.model.User;
import com.revature.AuthService.request.RegisterRequest;
import com.revature.AuthService.response.AuthResponse;
import com.revature.AuthService.response.LoginResponse;
import com.revature.AuthService.response.RegisterResponse;
import com.revature.AuthService.util.JwtUtil;

@Service
public class AuthService {
    private final UserService userService;
    private final PrivilegeService privilegeService;
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PrivilegeService privilegeService, PatientClient patientClient, DoctorClient doctorClient, JwtUtil jwtUtil) {
        this.userService = userService;
        this.privilegeService = privilegeService;
        this.patientClient = patientClient;
        this.doctorClient = doctorClient;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse validateLogin(String email, String password) {
        Optional<User> optionalUser = userService.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                String token = jwtUtil.generateToken(email, user.getUserId(), user.getPrivilege().getRoleName());
                LoginResponse loginResponse = new LoginResponse(user.getUserId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPrivilege(), token);

                return loginResponse;
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    @Transactional
    public RegisterResponse validateRegistration(RegisterRequest registerRequest) {
        if (registerRequest.getFirstName().isEmpty() || registerRequest.getLastName().isEmpty() || registerRequest.getEmail().isEmpty() || registerRequest.getPassword().isEmpty() || registerRequest.getPrivilegeId() == null) {
            throw new RuntimeException("Error: one or more required fields are empty");
        }
        Optional<User> optionalUser = userService.findUserByEmail(registerRequest.getEmail());
        if (optionalUser.isPresent()) {
            throw new RuntimeException("Invalid email: email already in use");
        }
        Optional<Privilege> optionalPrivilege = privilegeService.findById(registerRequest.getPrivilegeId());
        if (optionalPrivilege.isEmpty()) {
            throw new RuntimeException("Invalid privilege: privilege does not exist");
        }

        User user = new User(registerRequest.getEmail(), registerRequest.getPassword(), registerRequest.getFirstName(), registerRequest.getLastName(), optionalPrivilege.get());

        User newUser = userService.save(user);

        if (registerRequest.getPrivilegeId() == 1) {
            Patient patient = new Patient();
            patient.setUser(newUser);
            patientClient.createPatient(patient);
        }
        if(registerRequest.getPrivilegeId() == 2) {
            Doctor doctor = new Doctor();
            doctor.setUser(newUser);
            doctorClient.createDoctor(doctor);
        }

        return new RegisterResponse(newUser.getUserId(), newUser.getEmail(), newUser.getPrivilege());
    }

    public AuthResponse validateToken(String token){
        Integer id = jwtUtil.extractId(token);
        String privilege = jwtUtil.extractPrivilege(token);

        if (jwtUtil.validateToken(token)){
            return new AuthResponse(id, privilege, token, true);
        } else {
            return new AuthResponse(id, privilege, token, false);
        }
    }
}