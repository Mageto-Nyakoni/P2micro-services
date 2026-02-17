package com.revature.InfoService.dto.request;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
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
