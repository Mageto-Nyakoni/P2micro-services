package com.revature.smartAppointment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.revature.smartAppointment.Model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

//Testing to see if we're on Backend !
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // Used by DoctorService for dashboard queries
    // Joins through TimeSlot to find appointments for a specific doctor
    @Query(value = "SELECT a.* FROM appointments a " +
            "INNER JOIN time_slot ts ON a.slot_id = ts.slot_id " +
            "WHERE ts.doctor_id = :doctorId AND a.date_time_scheduled BETWEEN :start AND :end",
            nativeQuery = true)
    List<Appointment> findByDoctorDoctorIdAndDateTimeScheduledBetween(
            @Param("doctorId") Integer doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
