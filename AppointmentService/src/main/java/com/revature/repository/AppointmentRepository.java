package com.revature.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.revature.model.Appointment;
import com.revature.model.AppointmentStatus;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);
    
    List<Appointment> findByStatus(AppointmentStatus status);
}