package com.revature.AppointmentService.repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.model.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByDoctorId(Integer doctorId);

    List<Appointment> findByPatientId(Integer patientId);
    
    List<Appointment> findByStatus(AppointmentStatus status);

    Optional<Appointment> findByDoctorIdAndDateTimeScheduledBetween(Integer doctorId, LocalDateTime start, LocalDateTime end);

    Optional<Appointment> findByDoctorIdAndDateTimeScheduledAfter(Integer doctorId, LocalDateTime now);
}