package com.revature.InfoService.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.revature.InfoService.model.Doctor;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot {
    private Integer slotId;
    private Doctor doctor;
    private LocalDate dateAvailable;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private LocalDateTime createdAt;

    public TimeSlot(Doctor doctor, LocalDate dateAvailable, LocalTime startTime, LocalTime endTime) {
        this.doctor = doctor;
        this.dateAvailable = dateAvailable;
        this.startTime = startTime;
        this.endTime = startTime != null ? startTime.plusMinutes(30) : endTime;
        this.status = "AVAILABLE";
    }
}
