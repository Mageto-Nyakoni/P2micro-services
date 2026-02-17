package com.revature.AppointmentService.controller;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.revature.AppointmentService.dto.request.BookAppointmentRequestDto;
import com.revature.AppointmentService.dto.response.AppointmentDto;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

     private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }
//=================book====================
    @PostMapping
public Appointment create(@RequestBody BookAppointmentRequestDto request) {
    return service.bookAppointment(
            request.getPatientId(),
            request.getDoctorId(),
            request.getSlotId(),
            request.getAppointmentTypeId()
    );
}
//====================get doctor===================
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentDto> getDoctorAppointments(@PathVariable Long doctorId) {
        return service.getDoctorAppointments(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentDto> patientAppointments(@PathVariable Long patientId) {
        return service.getPatientAppointments(patientId);
    }

    @DeleteMapping("/{id}")
    public String cancel(@PathVariable Long id) {
        service.cancelAppointment(id);
        return "Appointment cancelled successfully";
    }
    
}
