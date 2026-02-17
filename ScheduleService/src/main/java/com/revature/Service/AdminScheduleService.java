package com.revature.Service;

import com.revature.Model.TimeSlot;
import com.revature.Repository.TimeSlotRepository;
import com.revature.Model.enums.TimeSlotStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AdminScheduleService {
    private final DoctorInfoClient doctorInfoClient;
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public AdminScheduleService(DoctorInfoClient doctorInfoClient, TimeSlotRepository timeSlotRepository) {
        this.doctorInfoClient = doctorInfoClient;
        this.timeSlotRepository = timeSlotRepository;
    }

    public TimeSlot addDoctorAvailability(Integer doctorId, LocalDate date, LocalTime start, LocalTime end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (!doctorInfoClient.doctorExists(doctorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found");
        }

        LocalTime correctedEnd = start.plusMinutes(30);
        TimeSlot slot = new TimeSlot(start, correctedEnd, date, doctorId);
        slot.setStatus(TimeSlotStatus.AVAILABLE);
        return timeSlotRepository.save(slot);
    }
}
