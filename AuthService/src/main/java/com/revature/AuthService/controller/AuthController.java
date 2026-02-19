package com.revature.AuthService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.AuthService.request.LoginRequest;
import com.revature.AuthService.request.RegisterRequest;
import com.revature.AuthService.response.AuthResponse;
import com.revature.AuthService.response.LoginResponse;
import com.revature.AuthService.response.RegisterResponse;
import com.revature.AuthService.service.AuthService;



@RestController
@RequestMapping("/smart-appointment/api/auth")
@CrossOrigin("*")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.validateLogin(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            RegisterResponse registerResponse = authService.validateRegistration(registerRequest);
            return ResponseEntity.ok(registerResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {

        try {
            String token = authHeader.substring(7);
            AuthResponse authResponse = authService.validateToken(token);
            
            return ResponseEntity.ok().body(authResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
    }
}
