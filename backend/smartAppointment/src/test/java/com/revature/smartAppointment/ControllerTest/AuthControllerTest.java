package com.revature.smartAppointment.ControllerTest;

import com.revature.smartAppointment.Controller.AuthController;
import com.revature.smartAppointment.Controller.Request.LoginRequest;
import com.revature.smartAppointment.Controller.Response.LoginResponse;
import com.revature.smartAppointment.Model.Privilege;
import com.revature.smartAppointment.Service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginSuccess() {
        Privilege privilege = new Privilege(1, "ADMIN");
        LoginResponse loginResponse = new LoginResponse(1, "test@email.com", privilege, "token123");

        when(authService.validateLogin("test@email.com", "pass123"))
                .thenReturn(loginResponse);

        LoginRequest request = new LoginRequest("test@email.com", "pass123");
        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value()); // ✅ updated
        assertEquals(loginResponse, response.getBody());
    }

    @Test
    void testLoginFailure() {
        when(authService.validateLogin("wrong@email.com", "wrong"))
                .thenThrow(new RuntimeException("Invalid username or password"));

        LoginRequest request = new LoginRequest("wrong@email.com", "wrong");
        ResponseEntity<?> response = authController.login(request);

        assertEquals(400, response.getStatusCode().value()); // ✅ updated
        assertEquals("Invalid username or password", response.getBody());
    }
}
