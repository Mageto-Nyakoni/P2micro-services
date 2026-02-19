package com.revature.AppointmentService.controller;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.AppointmentService.dto.request.BookAppointmentRequestDto;
import com.revature.AppointmentService.dto.response.AppointmentDto;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.service.AppointmentService;

@RestController
@RequestMapping("/smart-appointment/api/appointments")
@CrossOrigin("*")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    //=================book====================
    @PostMapping("/book")
    public ResponseEntity<Integer> create(@RequestBody BookAppointmentRequestDto request) {
        Appointment appointment = service.bookAppointment(
            request.getPatientId(),
            request.getDoctorId(),
            request.getSlotId(),
            request.getAppointmentTypeId()
        );

        return ResponseEntity.ok(appointment.getAppointmentId()); 
    }
    //====================get doctor===================
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDto>> getDoctorAppointments(@PathVariable Integer doctorId, @RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end, @RequestParam(required = false) LocalDateTime now) {
        if (start != null && end != null) {
            return ResponseEntity.ok(service.getDoctorAppointmentBetweenStartAndEnd(doctorId, start, end));
        } else if (start != null) {
            return ResponseEntity.ok(service.getDoctorAppointmentAfterNow(doctorId, now));
        } else {
            return ResponseEntity.ok(service.getDoctorAppointments(doctorId));
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDto>> patientAppointments(@PathVariable Integer patientId) {
        return ResponseEntity.ok(service.getPatientAppointments(patientId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancel(@PathVariable Integer id) {
        service.cancelAppointment(id);
        return ResponseEntity.ok("Appointment cancelled successfully");
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointment(@PathVariable Integer appointmentId) {
        try {
            return ResponseEntity.ok(service.getAppointmentById(appointmentId));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
