package com.revature.smartAppointment.Repository;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Service.DoctorService.DoctorAppointmentView;
import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Service.DoctorService.DoctorAppointmentView;

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
        // All appointments for a doctor ordered by date
    List<Appointment> findBySlotDoctorDoctorIdOrderByDateTimeScheduledAsc(Integer doctorId);

      //  Calendar: appointments for a doctor on a specific date
    @Query("""
       SELECT new com.revature.smartAppointment.dto.DoctorAppointmentView(
    p.user.firstName,
    p.user.lastName,
    p.age,
    a.dateTimeScheduled,
    a.status
)
FROM Appointment a
JOIN a.patient p
JOIN a.slot s
WHERE s.doctor.doctorId = :doctorId
  AND FUNCTION('DATE', a.dateTimeScheduled) = :date

    """)
    List<DoctorAppointmentView> findAppointmentsForDoctorByDate(
            @Param("doctorId") Integer doctorId,
            @Param("date") LocalDate date
    );
}
