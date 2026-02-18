package com.revature.InfoService.dto;

import java.time.LocalDateTime;

import com.revature.InfoService.model.Doctor;
import com.revature.InfoService.model.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private Long appointmentId;
    private Long doctorId;
    private Long patientId;
    private Long slotId;
    private Long appointmentTypeId;
    private LocalDateTime createdAt;
    private LocalDateTime dateTimeScheduled;
    private String status = "CONFIRMED";

    public Appointment(Long doctorId, Long slotId, AppointmentType appointmentType, Long patientId, LocalDateTime dateTimeScheduled) {
        this.doctor = doctor;
        this.slot = slot;
        this.appointmentType = appointmentType;
        this.patient = patient;
        this.dateTimeScheduled = dateTimeScheduled;
    }
}
