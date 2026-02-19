package com.revature.InfoService.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {
    private Integer appointmentId;
    private Integer doctorId;
    private Integer patientId;
    private Integer slotId;
    private AppointmentType appointmentType;
    private LocalDateTime createdAt;
    private LocalDateTime dateTimeScheduled;
    private String status = "CONFIRMED";

    public AppointmentDto(Integer doctorId, Integer slotId, AppointmentType appointmentType, Integer patientId, LocalDateTime dateTimeScheduled) {
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.appointmentType = appointmentType;
        this.patientId = patientId;
        this.dateTimeScheduled = dateTimeScheduled;
    }
}
