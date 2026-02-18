package com.revature.InfoService.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {
    private Long appointmentId;
    private Long doctorId;
    private Long patientId;
    private Long slotId;
    private Long appointmentTypeId;
    private LocalDateTime createdAt;
    private LocalDateTime dateTimeScheduled;
    private String status = "CONFIRMED";

    public AppointmentDto(Long doctorId, Long slotId, Long appointmentTypeId, Long patientId, LocalDateTime dateTimeScheduled) {
        this.doctorId = doctorId;
        this.slotId = slotId;
        this.appointmentTypeId = appointmentTypeId;
        this.patientId = patientId;
        this.dateTimeScheduled = dateTimeScheduled;
    }
}
