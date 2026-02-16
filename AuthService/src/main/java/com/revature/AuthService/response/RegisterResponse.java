package com.revature.AuthService.response;

import com.revature.AuthService.model.Privilege;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private Integer userId;
    private String email;
    private Privilege privilege;
}
