package com.revature.InfoService.dto;

import java.time.LocalDateTime;

import com.revature.InfoService.model.Doctor;
import com.revature.InfoService.model.Patient;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private Long appointmentId;
    private Doctor doctor;
    private Patient patient;
    private TimeSlot slot;
    private AppointmentType appointmentType;
    private LocalDateTime createdAt;
    private LocalDateTime dateTimeScheduled;
    private String status = "CONFIRMED";

    public Appointment(Doctor doctor, TimeSlot slot, AppointmentType appointmentType, Patient patient, LocalDateTime dateTimeScheduled) {
        this.doctor = doctor;
        this.slot = slot;
        this.appointmentType = appointmentType;
        this.patient = patient;
        this.dateTimeScheduled = dateTimeScheduled;
    }
}
