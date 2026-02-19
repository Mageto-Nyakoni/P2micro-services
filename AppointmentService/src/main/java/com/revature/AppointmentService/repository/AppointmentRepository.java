package com.revature.AppointmentService.repository;
import java.time.LocalDateTime;
import java.util.List;
<<<<<<< Updated upstream
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
=======
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.revature.AppointmentService.dto.response.DoctorAppointmentView;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.model.AppointmentStatus;
@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, Integer> {
  
    List<Appointment> findByPatientId(Integer patientId);
   
    List<Appointment> findByPatientIdAndStatus(
            Integer patientId,
            AppointmentStatus status);

    
    List<Appointment> findByDoctorIdOrderByDateTimeScheduledAsc(
            Integer doctorId);

    List<Appointment> findByDoctorIdAndDateTimeScheduledBetween(
            Integer doctorId,
            LocalDateTime start,
            LocalDateTime end);
            
    List<Appointment> findByPatientIdAndDateTimeScheduledGreaterThanEqualOrderByDateTimeScheduledAsc(
            Integer patientId,
            LocalDateTime start);

    List<Appointment> findByPatientIdAndDateTimeScheduledLessThanOrderByDateTimeScheduledDesc(
            Integer patientId,
            LocalDateTime end);
}
>>>>>>> Stashed changes
