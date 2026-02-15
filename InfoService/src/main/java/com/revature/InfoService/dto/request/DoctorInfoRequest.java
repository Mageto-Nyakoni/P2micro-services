package com.revature.InfoService.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorInfoRequest {
    private String gender;
    private String speciality;
    private int experience;
    private String bio;
}
