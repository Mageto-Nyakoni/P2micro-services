package com.revature.smartAppointment.ControllerTest;

import com.revature.smartAppointment.Controller.UserController;
import com.revature.smartAppointment.Model.Privilege;
import com.revature.smartAppointment.Model.User;
import com.revature.smartAppointment.Service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetUserReturnsUserWhenExists() {
        Privilege privilege = new Privilege(1, "ADMIN");
        User user = new User(1, "test@email.com", "pass123", "John", "Doe", privilege);

        when(userService.findById(1)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUser(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserReturnsBadRequestWhenNotFound() {
        when(userService.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUser(1);

        assertEquals(400, response.getStatusCode().value());
        assertNull(response.getBody());
    }
}