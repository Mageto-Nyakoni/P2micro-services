package com.revature.InfoService.dto;

import java.time.LocalTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotDoctorView {
    private Integer slotId;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeSlotDoctorView(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}