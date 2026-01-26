package com.revature.smartAppointment.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.revature.smartAppointment.Model.AvailabilityWindow;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
import com.revature.smartAppointment.dto.AppointmentDto;

@Service
public class TimeSlotService {

  
    @Autowired
    private TimeSlotRepository timeSlotRepository;
     public TimeSlotService(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }
    public List<TimeSlot> getAvailableSlotsForDoctor(Integer doctorId) {
        return timeSlotRepository.findByDoctorDoctorId(doctorId).stream()
                .filter(slot -> slot.getStatus() == TimeSlotStatus.AVAILABLE)
                .toList();
    }

    public List<TimeSlot> getAvailableSlots() {
        return timeSlotRepository.findByStatus(TimeSlotStatus.AVAILABLE);
    }
    
      // New method for public API
    public List<Map<String, Object>> getAvailableSlotsPublic(Integer doctorId, LocalDate date, LocalDate from, LocalDate to) {
        // Fetch all available slots first
        List<TimeSlot> slots = timeSlotRepository.findAll().stream()
                .filter(slot -> slot.getStatus() == TimeSlotStatus.AVAILABLE)
                .collect(Collectors.toList());

        // Apply filters if provided
        if (doctorId != null) {
            slots = slots.stream()
                    .filter(slot -> slot.getDoctor().getDoctorId().equals(doctorId))
                    .collect(Collectors.toList());
        }

        if (date != null) {
            slots = slots.stream()
                    .filter(slot -> slot.getDateAvailable().equals(date))
                    .collect(Collectors.toList());
        }

        if (from != null) {
            slots = slots.stream()
                    .filter(slot -> !slot.getDateAvailable().isBefore(from))
                    .collect(Collectors.toList());
        }

        if (to != null) {
            slots = slots.stream()
                    .filter(slot -> !slot.getDateAvailable().isAfter(to))
                    .collect(Collectors.toList());
        }

        // Map slots to a simple structure for the public API
        return slots.stream().map(slot -> {
            Map<String, Object> map = new HashMap<>();
            map.put("slotId", slot.getSlotId());
            map.put("doctorId", slot.getDoctor().getDoctorId());
            map.put("startTime", slot.getStartTime());
            map.put("endTime", slot.getEndTime());
            map.put("date", slot.getDateAvailable());
            return map;
        }).collect(Collectors.toList());
    }



    public void generateSlotsForWindow(AvailabilityWindow window) {
        int slotDurationMinutes = 30; // each slot 30 minutes
        LocalTime currentTime = window.getStartTime();

        while (currentTime.isBefore(window.getEndTime())) {
            TimeSlot slot = new TimeSlot();
            slot.setDoctor(window.getDoctor());
            slot.setDateAvailable(window.getDate());
            slot.setStartTime(currentTime);
            slot.setEndTime(currentTime.plusMinutes(slotDurationMinutes));
            slot.setStatus(TimeSlotStatus.AVAILABLE);
            timeSlotRepository.save(slot);

            currentTime = currentTime.plusMinutes(slotDurationMinutes);
        }
    }
}