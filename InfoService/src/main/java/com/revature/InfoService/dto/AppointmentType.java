package com.revature.InfoService.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentType {
    private Integer typeId;
    private String name;
    private Integer estimatedTime; // minutes
    private String description;

    public AppointmentType(String name, Integer estimatedTime, String description) {
        this.name = name;
        this.estimatedTime = estimatedTime;
        this.description = description;
    }
}
