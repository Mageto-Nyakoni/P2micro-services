package com.revature.smartAppointment.Repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;

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

      List<Appointment> findBySlotDoctorDoctorIdOrderByDateTimeScheduledAsc(Integer doctorId);
}