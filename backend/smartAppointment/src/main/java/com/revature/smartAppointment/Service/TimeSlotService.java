package com.revature.smartAppointment.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
import com.revature.smartAppointment.dto.AppointmentDto;

@Service
public class TimeSlotService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;
 public List<TimeSlot> getAvailableSlotsForDoctor(Integer doctorId) {
        return timeSlotRepository.findByDoctorDoctorId(doctorId).stream()
                .filter(slot -> slot.getStatus() == TimeSlotStatus.AVAILABLE)
                .toList();
    }
  
}
