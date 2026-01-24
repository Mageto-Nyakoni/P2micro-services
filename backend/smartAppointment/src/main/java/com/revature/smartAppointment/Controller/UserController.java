package com.revature.smartAppointment.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.smartAppointment.Controller.Response.UserTableResponse;
import com.revature.smartAppointment.Model.User;
import com.revature.smartAppointment.Service.UserService;

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

    @GetMapping("/table")
    public ResponseEntity<List<UserTableResponse>> getUsersForTable() {
        return ResponseEntity.ok(userService.getUsersForTable());
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<User> updateUser(@PathVariable int user_id, @RequestBody User userInfo) {
        Optional<User> optionalUser = userService.findById(user_id);
        if (optionalUser.isPresent()) {
            User updatedUser = userService.updateById(user_id, userInfo);
            return ResponseEntity.status(200).body(updatedUser);
        }
        return ResponseEntity.status(400).build();
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int user_id) {
        userService.deleteById(user_id);
        return ResponseEntity.ok().build();
    }
}
