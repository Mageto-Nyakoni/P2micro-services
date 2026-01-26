package com.revature.smartAppointment.Repository;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

      List<Appointment> findByPatientPatientIdAndDateTimeScheduledGreaterThanEqualOrderByDateTimeScheduledAsc(
            Integer patientId,
            LocalDateTime start
      );

      List<Appointment> findByPatientPatientIdAndDateTimeScheduledLessThanOrderByDateTimeScheduledDesc(
            Integer patientId,
            LocalDateTime end
      );

      @Query("""
            SELECT DISTINCT a
            FROM Appointment a
            JOIN FETCH a.slot s
            JOIN FETCH s.doctor d
            JOIN FETCH d.user u
            JOIN FETCH a.appointmentType t
            WHERE a.patient.patientId = :patientId
              AND a.dateTimeScheduled >= :now
              AND a.status NOT IN :excludedStatuses
            ORDER BY a.dateTimeScheduled ASC
            """)
      List<Appointment> findCurrentForPatientWithDetails(
            @Param("patientId") Integer patientId,
            @Param("now") LocalDateTime now,
            @Param("excludedStatuses") List<AppointmentStatus> excludedStatuses
      );

      @Query("""
            SELECT DISTINCT a
            FROM Appointment a
            JOIN FETCH a.slot s
            JOIN FETCH s.doctor d
            JOIN FETCH d.user u
            JOIN FETCH a.appointmentType t
            WHERE a.patient.patientId = :patientId
              AND (a.dateTimeScheduled < :now OR a.status IN :historyStatuses)
            ORDER BY a.dateTimeScheduled DESC
            """)
      List<Appointment> findHistoryForPatientWithDetails(
            @Param("patientId") Integer patientId,
            @Param("now") LocalDateTime now,
            @Param("historyStatuses") List<AppointmentStatus> historyStatuses
      );

}
