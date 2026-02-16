package com.revature.AuthService.response;

import com.revature.AuthService.model.Privilege;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private Privilege privilege;
    private String token;
}
