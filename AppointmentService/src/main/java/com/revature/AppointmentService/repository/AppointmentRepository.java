package com.revature.AppointmentService.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.model.AppointmentStatus;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByStatus(AppointmentStatus status);
}