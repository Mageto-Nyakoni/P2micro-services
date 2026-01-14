package com.revature.smartAppointment.Repository;

import com.revature.smartAppointment.Model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    List<TimeSlot> findByDoctorId(Integer doctorId);
}

