package com.revature.ScheduleService.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.revature.ScheduleService.model.TimeSlot;
import com.revature.ScheduleService.model.enums.AppointmentStatus;
import com.revature.ScheduleService.model.enums.TimeSlotStatus;
import com.revature.ScheduleService.service.AdminAppointmentService;
import com.revature.ScheduleService.service.AdminScheduleService;
import com.revature.ScheduleService.service.AuthValidationService;
import com.revature.ScheduleService.service.TimeSlotService;

@RestController
@RequestMapping("smart-appointment/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final AdminAppointmentService adminAppointmentService;
    private final AdminScheduleService adminScheduleService;
    private final TimeSlotService timeSlotService;
    private final AuthValidationService authValidationService;

    public AdminController(
            AdminAppointmentService adminAppointmentService,
            AdminScheduleService adminScheduleService,
            TimeSlotService timeSlotService,
            AuthValidationService authValidationService
    ) {
        this.adminAppointmentService = adminAppointmentService;
        this.adminScheduleService = adminScheduleService;
        this.timeSlotService = timeSlotService;
        this.authValidationService = authValidationService;
    }

    //  View ALL appointments (system-wide)
    @GetMapping("/appointments")
    public ResponseEntity<List<Map<String, Object>>> getAllAppointments(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(adminAppointmentService.getAppointments(authHeader, status));
    }

    //  Accept appointment
    @PutMapping("/appointments/{id}/accept")
    public ResponseEntity<Map<String, Object>> accept(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(authHeader, id, AppointmentStatus.CONFIRMED)
        );
    }

    //  Cancel appointment
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancel(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(authHeader, id, AppointmentStatus.CANCELLED)
        );
    }

    //  Deny appointment
    @PatchMapping("/appointments/{id}/deny")
    public ResponseEntity<Map<String, Object>> deny(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(authHeader, id, AppointmentStatus.DENIED)
        );
    }

    //  Reschedule appointment
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<Map<String, Object>> reschedule(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestParam LocalDate date,
            @RequestParam LocalTime time
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
            adminAppointmentService.reschedule(
                    authHeader,
                    id,
                    java.time.LocalDateTime.of(date, time)
            )
        );
    }

    @PostMapping("/doctors/{doctorId}/schedule")
    public TimeSlot addSchedule(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer doctorId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime start,
            @RequestParam LocalTime end
    ) {
        validateAdmin(authHeader);
        return adminScheduleService.addDoctorAvailability(doctorId, date, start, end);
    }

    @GetMapping("/doctors/{doctorId}/time-slots")
    public ResponseEntity<List<TimeSlotService.PublicTimeSlotView>> getDoctorSlots(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer doctorId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(timeSlotService.getSlotsForDoctorRange(doctorId, from, to));
    }

    @GetMapping("/time-slots")
    public ResponseEntity<List<TimeSlotService.PublicTimeSlotView>> getAllTimeSlots(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) TimeSlotStatus status,
            @RequestParam(required = false) Integer doctorId
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(timeSlotService.getAdminTimeSlots(doctorId, from, to, status));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<Map<String, Object>>> getStaff(
            @RequestHeader("Authorization") String authHeader
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(adminAppointmentService.getStaff(authHeader));
    }

    private void validateAdmin(String authHeader) {
        authValidationService.validateAdmin(authHeader);
    }
}
