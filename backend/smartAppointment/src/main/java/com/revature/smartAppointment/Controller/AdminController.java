package com.revature.smartAppointment.Controller;

import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Service.admin.AdminAppointmentService;
import com.revature.smartAppointment.Service.admin.AdminScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("smart-appointment/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final AdminAppointmentService adminAppointmentService;
    private final AdminScheduleService adminScheduleService;

    @Autowired
    public AdminController(AdminAppointmentService adminAppointmentService, AdminScheduleService adminScheduleService) {
        this.adminAppointmentService = adminAppointmentService;
        this.adminScheduleService = adminScheduleService;
    }

    //  View ALL appointments (system-wide)
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(adminAppointmentService.getAllAppointments());
    }

    //  Accept appointment
    @PutMapping("/appointments/{id}/accept")
    public ResponseEntity<Appointment> accept(@PathVariable Integer id) {
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(id, AppointmentStatus.CONFIRMED)
        );
    }

    //  Cancel appointment
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<Appointment> cancel(@PathVariable Integer id) {
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(id, AppointmentStatus.CANCELLED)
        );
    }

    //  Reschedule appointment
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<Appointment> reschedule(@PathVariable Integer id, @RequestParam LocalDate date, @RequestParam LocalTime time) {
        return ResponseEntity.ok(
            adminAppointmentService.reschedule(
                    id,
                    java.time.LocalDateTime.of(date, time)
            )
        );
    }

    @PostMapping("/doctors/{doctorId}/schedule")
    public TimeSlot addSchedule(@PathVariable Integer doctorId, @RequestParam LocalDate date, @RequestParam LocalTime start, @RequestParam LocalTime end) {
        return adminScheduleService.addDoctorAvailability(doctorId, date, start, end);
    }
}
