package com.revature.AppointmentService.controller;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.revature.AppointmentService.dto.request.BookAppointmentRequestDto;
import com.revature.AppointmentService.dto.response.AppointmentDto;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.service.AppointmentService;

@RestController
@RequestMapping("smart-appointment/api/appointments")
@CrossOrigin("*")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    //=================book====================
    @PostMapping("/book")
    public Integer create(@RequestBody BookAppointmentRequestDto request) {
        Appointment appointment = service.bookAppointment(
            request.getPatientId(),
            request.getDoctorId(),
            request.getSlotId(),
            request.getAppointmentTypeId()
        );

        return appointment.getAppointmentId(); 
    }
    //====================get doctor===================
    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentDto> getDoctorAppointments(@PathVariable Integer doctorId, @RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end, @RequestParam(required = false) LocalDateTime now) {
        if (start != null && end != null) {
            return service.getDoctorAppointmentBetweenStartAndEnd(doctorId, start, end);
        } else if (start != null) {
            return service.getDoctorAppointmentAfterNow(doctorId, now);
        } else {
            return service.getDoctorAppointments(doctorId);
        }
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentDto> patientAppointments(@PathVariable Integer patientId) {
        return service.getPatientAppointments(patientId);
    }

    @DeleteMapping("/{id}")
    public String cancel(@PathVariable Integer id) {
        service.cancelAppointment(id);
        return "Appointment cancelled successfully";
    }

    @GetMapping("/{appointmentId}")
    public Appointment getAppointment(@PathVariable Integer appointmentId) {
        return service.getAppointmentById(appointmentId);
    }

}
