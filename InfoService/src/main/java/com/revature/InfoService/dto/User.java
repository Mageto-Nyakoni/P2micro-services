package com.revature.InfoService.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Privilege privilege;

    public User(String email, String password, String firstName, String lastName, Privilege privilege) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.privilege = privilege;
    }
}