package com.revature.ScheduleService.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.ScheduleService.dto.DoctorAvailabilityDto;
import com.revature.ScheduleService.service.AvailabilityWindowService;

@RestController
@RequestMapping("/smart-appointment/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AvailabilityController {

    private final AvailabilityWindowService availabilityWindowService;

    public AvailabilityController(AvailabilityWindowService availabilityWindowService) {
        this.availabilityWindowService = availabilityWindowService;
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, List<DoctorAvailabilityDto>>> getAvailability() {
        Map<String, List<DoctorAvailabilityDto>> availability = availabilityWindowService.getAvailabilityByDate();
        return ResponseEntity.ok(availability);
    }
}
