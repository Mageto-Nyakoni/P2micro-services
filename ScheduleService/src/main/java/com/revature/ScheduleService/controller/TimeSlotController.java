package com.revature.ScheduleService.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.revature.ScheduleService.model.TimeSlot;
import com.revature.ScheduleService.model.enums.TimeSlotStatus;
import com.revature.ScheduleService.repository.TimeSlotRepository;
import com.revature.ScheduleService.service.TimeSlotService;
import com.revature.ScheduleService.dto.SlotDto;

@RestController
@RequestMapping("/smart-appointment/api/slots")
@CrossOrigin(origins = "http://localhost:5173")
public class TimeSlotController {

    private final TimeSlotRepository timeSlotRepository;
    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotRepository timeSlotRepository, TimeSlotService timeSlotService) {
        this.timeSlotRepository = timeSlotRepository;
        this.timeSlotService = timeSlotService;
    }

    @GetMapping("/doctor/{doctorId}")
    public List<SlotDto> getDoctorSlotsByDate(
            @PathVariable Integer doctorId,
            @RequestParam String date
    ) {
        LocalDate localDate = LocalDate.parse(date);

        return timeSlotRepository
                .findByDoctorIdAndDateAvailableAndStatusOrderByStartTimeAsc(
                        doctorId,
                        localDate,
                        TimeSlotStatus.AVAILABLE
                )
                .stream()
                .map(slot -> {
                SlotDto dto = new SlotDto();
                dto.setSlotId(slot.getSlotId());
                dto.setStartTime(slot.getStartTime().toString());
                dto.setEndTime(slot.getEndTime().toString());
                dto.setAvailable(slot.getStatus() == TimeSlotStatus.AVAILABLE);
                return dto;
            })
            .toList();
    }

    @PatchMapping("/{slotId}/status")
    public ResponseEntity<TimeSlot> updateSlotStatus(@PathVariable Integer slotId, @RequestParam TimeSlotStatus status) {
        TimeSlot slot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time slot not found"));

        TimeSlot updated = timeSlotService.updateStatus(slot, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("{slotId}")
    public ResponseEntity<TimeSlot> getTimeSlotById(@PathVariable Integer slotId) {
        TimeSlot slot = timeSlotService.findById(slotId);
        if (slot != null) {
                return ResponseEntity.ok(slot);
        }
        return ResponseEntity.notFound().build();
    }
}
