package com.revature.smartAppointment.Controller.Request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientInfoRequest {
    private int age;
    private String gender;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String address;
    private String bloodType;
    private String[] allergies;
}
