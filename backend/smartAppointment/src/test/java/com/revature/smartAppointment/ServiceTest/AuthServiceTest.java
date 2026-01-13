package com.revature.smartAppointment.ServiceTest;

import com.revature.smartAppointment.Controller.Request.RegisterRequest;
import com.revature.smartAppointment.Controller.Response.LoginResponse;
import com.revature.smartAppointment.Controller.Response.RegisterResponse;
import com.revature.smartAppointment.Model.Privilege;
import com.revature.smartAppointment.Model.User;
import com.revature.smartAppointment.Service.AuthService;
import com.revature.smartAppointment.Service.PrivilegeService;
import com.revature.smartAppointment.Service.UserService;
import com.revature.smartAppointment.Util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PrivilegeService privilegeService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void testValidateLoginSuccess() {
        Privilege privilege = new Privilege(1, "ADMIN");
        User user = new User(1, "test@test.com", "pass123", "John", "Doe", privilege);

        when(userService.findUserByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test@test.com", "ADMIN")).thenReturn("token123");

        LoginResponse response = authService.validateLogin("test@test.com", "pass123");

        assertEquals(user.getUserId(), response.getUserId());
        assertEquals("token123", response.getToken());
        assertEquals(privilege, response.getPrivilege());
    }

    @Test
    void testValidateLoginInvalidPassword() {
        User user = new User(1, "test@test.com", "pass123", "John", "Doe", null);
        when(userService.findUserByEmail("test@test.com")).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.validateLogin("test@test.com", "wrongpass"));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateLoginUserNotFound() {
        when(userService.findUserByEmail("test@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.validateLogin("test@test.com", "pass123"));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateRegistrationSuccess() {
        Privilege privilege = new Privilege(1, "ADMIN");
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@test.com", "pass123", 1);

        when(userService.findUserByEmail("test@test.com")).thenReturn(Optional.empty());
        when(privilegeService.findById(1)).thenReturn(Optional.of(privilege));
        User savedUser = new User(1, "test@test.com", "pass123", "John", "Doe", privilege);
        when(userService.save(any(User.class))).thenReturn(savedUser);

        RegisterResponse response = authService.validateRegistration(request);

        assertEquals(1, response.getUserId());
        assertEquals("test@test.com", response.getEmail());
        assertEquals(privilege, response.getPrivilege());
    }

    @Test
    void testValidateRegistrationEmailExists() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@test.com", "pass123", 1);
        when(userService.findUserByEmail("test@test.com")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.validateRegistration(request));

        assertEquals("Invalid email: email already in use", exception.getMessage());
    }

    @Test
    void testValidateRegistrationPrivilegeNotFound() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "test@test.com", "pass123", 1);
        when(userService.findUserByEmail("test@test.com")).thenReturn(Optional.empty());
        when(privilegeService.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.validateRegistration(request));

        assertEquals("Invalid privilege: privilege does not exist", exception.getMessage());
    }

    @Test
    void testValidateRegistrationMissingFields() {
        RegisterRequest request = new RegisterRequest("", "Doe", "test@test.com", "pass123", 1);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authService.validateRegistration(request));

        assertEquals("Error: one or more required fields are empty", exception.getMessage());
    }
}