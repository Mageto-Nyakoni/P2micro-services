package com.revature.smartAppointment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.smartAppointment.Model.TimeSlot;


@Repository
public interface TimeSlotRepository extends JpaRepository< TimeSlot, Integer> {
}
