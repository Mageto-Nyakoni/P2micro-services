package com.revature.smartAppointment.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;

import com.revature.smartAppointment.Model.TimeSlot;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    List<TimeSlot> findByDoctorDoctorId(Integer doctorId);
    
    

    boolean existsByDoctorAndDateAvailableAndStartTime(
            com.revature.smartAppointment.Model.Doctor doctor,
            java.time.LocalDate dateAvailable,
            java.time.LocalTime startTime
    );
     List<TimeSlot> findByStatus(TimeSlotStatus status);
    /*List<TimeSlot> findByPatientIdAndStatusOrderByStartAt(
        Integer patientId,
        TimeSlotStatus status
           );*/
}
