package com.revature.smartAppointment.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import com.revature.smartAppointment.Model.enums.TimeSlotStatus;

@Entity
@Table(name = "time_slot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column (name = "slot_id")
    private Integer slotId;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "start_time")
    private LocalTime startTime;

    @Column (name = "end_time")
    private LocalTime endTime;

    @Column (name = "date_available")
    private LocalDate dateAvailable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public TimeSlot(LocalTime startTime, LocalTime endTime, LocalDate dateAvailable, Doctor doctor) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dateAvailable = dateAvailable;
        this.doctor = doctor;
    }
}
