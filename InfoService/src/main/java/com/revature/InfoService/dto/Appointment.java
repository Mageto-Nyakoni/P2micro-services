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
    private Integer appointmentId;
    private Doctor doctor;
    private TimeSlot slot;
    private AppointmentType appointmentType;
    private Patient patient;
    private LocalDateTime createdAt;
    private LocalDateTime dateTimeScheduled;
    private String status;

    public Appointment(Doctor doctor, TimeSlot slot, AppointmentType appointmentType, Patient patient, LocalDateTime dateTimeScheduled) {
        this.doctor = doctor;
        this.slot = slot;
        this.appointmentType = appointmentType;
        this.patient = patient;
        this.dateTimeScheduled = dateTimeScheduled;
    }
}
