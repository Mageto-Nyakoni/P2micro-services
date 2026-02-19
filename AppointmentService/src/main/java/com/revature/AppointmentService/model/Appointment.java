package com.revature.AppointmentService.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;

    @Column(name = "doctor_id", nullable = false)
    private Integer doctorId;

    @Column(name = "slot_id", nullable = false)
    private Integer slotId;

    @Column(name = "type_id", nullable = false)
    private Integer appointmentTypeId;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "date_time_scheduled")
    private LocalDateTime dateTimeScheduled;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = AppointmentStatus.CONFIRMED;
    }
}
