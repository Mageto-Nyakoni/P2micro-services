package com.revature.AuthService.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    private Integer userID;
    private Integer patientId;
    private Integer age;
    private String gender;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;
    private BloodType bloodType;
    private List<Allergy> allergies = new ArrayList<>();
    
}
