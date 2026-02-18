package com.revature.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.Service.AvailabilityService;
import com.revature.Service.AvailabilityWindowService;
import com.revature.dto.DoctorAvailabilityDto;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/smart-appointment/api")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;
     
    @Autowired
    private AvailabilityWindowService availabilityWindowService;


    @Autowired
    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, List<DoctorAvailabilityDto>>> getAvailability() {
        Map<String, List<DoctorAvailabilityDto>> availability = availabilityWindowService.getAvailabilityByDate();
        return ResponseEntity.ok(availability);
    }
}
