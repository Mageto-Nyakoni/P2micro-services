package com.revature.controller;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.revature.dto.request.BookAppointmentRequestDto;
import com.revature.model.Appointment;
import com.revature.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

     private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
public Appointment create(@RequestBody BookAppointmentRequestDto request) {
    return service.bookAppointment(
            request.getPatientId(),
            request.getDoctorId(),
            request.getSlotId(),
            request.getAppointmentTypeId()
    );
}

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> doctorAppointments(@PathVariable Long doctorId) {
        return service.getDoctorAppointments(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    public List<Appointment> patientAppointments(@PathVariable Long patientId) {
        return service.getPatientAppointments(patientId);
    }

    @DeleteMapping("/{id}")
    public String cancel(@PathVariable Long id) {
        service.cancelAppointment(id);
        return "Appointment cancelled successfully";
    }
    
}
