package com.revature.AppointmentService.model;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    //  Store only IDs (microservice safe)
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    

    @Column(name = "slot_id", nullable = false)
    private Long slotId;

    @Column(name = "type_id", nullable = false)
    private Long appointmentTypeId;

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
