package com.revature.InfoService.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDoctorView {
    private Integer appointmentId;
    private String patientFirstName;
    private String patientLastName;
    private String appointmentType;
    private LocalDateTime scheduledDateTime;
    private Integer estimatedDurationMinutes;
    private String status;

    public AppointmentDoctorView(String patientFirstName, String patientLastName, String appointmentType, LocalDateTime scheduledDateTime, Integer estimatedDurationMinutes, String status) {
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.appointmentType = appointmentType;
        this.scheduledDateTime = scheduledDateTime;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.status = status;
    }
}