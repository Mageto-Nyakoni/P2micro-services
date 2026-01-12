package com.revature.smartAppointment.Service;

import com.revature.smartAppointment.Controller.Response.LoginResponse;
import com.revature.smartAppointment.Model.User;
import com.revature.smartAppointment.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private UserService userService;

    private JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
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
}
