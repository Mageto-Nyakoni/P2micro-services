package com.revature.smartAppointment.Repository;
import java.util.List;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.smartAppointment.Model.Appointment;

//Testing to see if we're on Backend !
@Repository
public interface AppointmentRepository extends JpaRepository< Appointment, Integer> {

      List<Appointment> findBySlotDoctorDoctorIdAndDateTimeScheduledBetween(
            Integer doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

}
