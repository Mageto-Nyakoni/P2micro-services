package com.revature.ScheduleService.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.ScheduleService.dto.AvailabilityWindowDTO;
import com.revature.ScheduleService.service.AuthValidationService;
import com.revature.ScheduleService.service.AvailabilityWindowService;

@RestController
@RequestMapping("/smart-appointment/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAvailabilityController {

    private final AvailabilityWindowService availabilityWindowService;
    private final AuthValidationService authValidationService;

    public AdminAvailabilityController(
            AvailabilityWindowService availabilityWindowService,
            AuthValidationService authValidationService
    ) {
        this.availabilityWindowService = availabilityWindowService;
        this.authValidationService = authValidationService;
    }
    
   /*  @GetMapping("/{doctorId}/availability-windows")
public List<AvailabilityWindow> getAvailabilityWindows(
        @PathVariable Integer doctorId
) {
    return availabilityWindowService.getWindowsForDoctor(doctorId);
}*/
    
    @GetMapping("/doctors/{doctorId}/availability-windows")
    public ResponseEntity<List<AvailabilityWindowDTO>> getAvailabilityWindows(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer doctorId
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(availabilityWindowService.getWindowsForDoctor(doctorId));
    }

    @PostMapping("/doctors/{doctorId}/availability-windows")
    public ResponseEntity<AvailabilityWindowDTO> createAvailabilityWindow(
            @PathVariable Integer doctorId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AvailabilityWindowRequest request
    ) {
        validateAdmin(authHeader);
        AvailabilityWindowDTO window = availabilityWindowService.createWindowDto(
                doctorId,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(window);
    }

    @DeleteMapping("/availability-windows/{windowId}")
    public ResponseEntity<Map<String, Object>> deleteAvailabilityWindow(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer windowId
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(availabilityWindowService.deactivateWindow(windowId));
    }

    private void validateAdmin(String authHeader) {
        authValidationService.validateAdmin(authHeader);
    }

    public static class AvailabilityWindowRequest {

        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }
    }
}
