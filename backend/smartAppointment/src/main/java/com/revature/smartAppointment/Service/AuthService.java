package com.revature.smartAppointment.Service;

import com.revature.smartAppointment.Controller.Request.RegisterRequest;
import com.revature.smartAppointment.Controller.Response.LoginResponse;
import com.revature.smartAppointment.Controller.Response.RegisterResponse;
import com.revature.smartAppointment.Model.Privilege;
import com.revature.smartAppointment.Model.User;
import com.revature.smartAppointment.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private UserService userService;
    private PrivilegeService privilegeService;

    private JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserService userService, PrivilegeService privilegeService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.privilegeService = privilegeService;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse validateLogin(String email, String password) {
        Optional<User> optionalUser = userService.findUserByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(password)) {
                String token = jwtUtil.generateToken(email, user.getPrivilege().getRoleName());
                LoginResponse loginResponse = new LoginResponse(user.getUserId(), user.getEmail(), user.getPrivilege(), token);

                return loginResponse;
            }
        }
        throw new RuntimeException("Invalid username or password");
    }

    public RegisterResponse validateRegistration(RegisterRequest registerRequest) {
        if (registerRequest.getFirstName().length() == 0 || registerRequest.getLastName().length() == 0 || registerRequest.getEmail().length() == 0 || registerRequest.getPassword().length() == 0 || registerRequest.getPrivilegeId() == null) {
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
        return new RegisterResponse(newUser.getUserId(), newUser.getEmail(), newUser.getPrivilege());
    }
}
