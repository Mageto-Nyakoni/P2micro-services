package com.revature.AuthService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Speciality {
    private Integer specialityId;
    private String specialityName;
    private String description; 
}
