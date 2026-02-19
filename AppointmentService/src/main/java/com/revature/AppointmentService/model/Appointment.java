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

    //  Store only IDs (microservice safe)
    @Column(name = "doctor_id", nullable = false)
    private Integer doctorId;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "slot_id", nullable = false)
    private Integer slotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private AppointmentType appointmentType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "date_time_scheduled", nullable = false)
    private LocalDateTime dateTimeScheduled;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = AppointmentStatus.CONFIRMED;
    }

    public Appointment(Integer doctorId, Integer patientId, Integer slotId, AppointmentType appointmentType, LocalDateTime createdAt, LocalDateTime dateTimeScheduled) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.slotId = slotId;
        this.appointmentType = appointmentType;
        this.createdAt = createdAt;
        this.dateTimeScheduled = dateTimeScheduled;
    }

    public Appointment(Integer doctorId, Integer patientId, Integer slotId, AppointmentType appointmentType, LocalDateTime dateTimeScheduled, AppointmentStatus status) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.slotId = slotId;
        this.appointmentType = appointmentType;
        this.dateTimeScheduled = dateTimeScheduled;
        this.status = status;
    }
}
