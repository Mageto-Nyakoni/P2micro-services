package com.revature.AuthService.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTableResponse {
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String privilege;
    // private String speciality;
    // private Integer experience;
    // private String gender;
    // private String bio;
}
