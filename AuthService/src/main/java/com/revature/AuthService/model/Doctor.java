package com.revature.AuthService.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private Integer doctorId;
    private User user;
    private Integer experienceYears;
    private String gender;
    private Speciality speciality;
    private String bio;
}
