package com.revature.smartAppointment.Controller;

import com.revature.smartAppointment.Model.User;

import com.revature.smartAppointment.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/smart-appointment/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<User> getUser(@PathVariable int user_id) {
        Optional<User> optionalUser = userService.findById(user_id);
        if (optionalUser.isPresent()) {
            return ResponseEntity.status(200).body(optionalUser.get());
        }
        return ResponseEntity.status(400).build();
    }
}
