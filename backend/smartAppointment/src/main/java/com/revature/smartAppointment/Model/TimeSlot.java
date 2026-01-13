package com.revature.smartAppointment.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import com.revature.smartAppointment.Model.Doctor;

@Entity
@Table(name = "time_slot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TimeSlot {
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
@Column (name="slot_id")
private Integer slotId;

@Column (name="created_at")
private LocalDateTime createdAt;

@Column (name="start_time")
private LocalDateTime startTime;

@Column (name="end_time")
private LocalDateTime endTime;

@Column (name="date_available")
private LocalDate dateAvailable;

@ManyToOne(optional = false)
@JoinColumn(name = "doctor_id", nullable = false)
private Doctor doctor;



}
