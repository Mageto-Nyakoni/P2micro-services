package com.revature.smartAppointment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.smartAppointment.Model.TimeSlot;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    // Used by DoctorService to fetch a doctor's slots
    List<TimeSlot> findByDoctorDoctorId(Integer doctorId);
}
