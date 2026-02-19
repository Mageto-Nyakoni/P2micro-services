package com.revature.ScheduleService.service;

import com.revature.ScheduleService.model.TimeSlot;
import com.revature.ScheduleService.repository.TimeSlotRepository;
import com.revature.ScheduleService.model.enums.TimeSlotStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AdminScheduleService {
    private final DoctorInfoService doctorInfoService;
    private final TimeSlotRepository timeSlotRepository;

    public AdminScheduleService(DoctorInfoService doctorInfoService, TimeSlotRepository timeSlotRepository) {
        this.doctorInfoService = doctorInfoService;
        this.timeSlotRepository = timeSlotRepository;
    }

    public TimeSlot addDoctorAvailability(Integer doctorId, LocalDate date, LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (!doctorInfoService.doctorExists(doctorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found");
        }

        LocalTime correctedEnd = start.plusMinutes(30);
        TimeSlot slot = new TimeSlot(start, correctedEnd, date, doctorId);
        slot.setStatus(TimeSlotStatus.AVAILABLE);
        return timeSlotRepository.save(slot);
    }
}
