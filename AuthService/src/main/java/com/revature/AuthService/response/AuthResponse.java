package com.revature.AuthService.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    Integer userId;
    String privilege;
    String token;
    Boolean isValid;
}
