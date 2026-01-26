package com.revature.smartAppointment.Repository;
import java.util.List;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

//Testing to see if we're on Backend !
@Repository
public interface AppointmentRepository extends JpaRepository< Appointment, Integer> {

      List<Appointment> findBySlotDoctorDoctorIdAndDateTimeScheduledBetween(
            Integer doctorId,
            LocalDateTime start,
            LocalDateTime end
    );
       List<Appointment> findByPatient_PatientId(Integer patientId);
      List<Appointment> findByPatient_PatientIdAndStatus(Integer patientId, AppointmentStatus status);
 List<Appointment> findBySlotDoctorDoctorIdAndDateTimeScheduledAfter(Integer doctorId, LocalDateTime dateTime);
}