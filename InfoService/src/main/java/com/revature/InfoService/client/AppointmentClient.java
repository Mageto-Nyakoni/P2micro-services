package com.revature.InfoService.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.revature.InfoService.dto.AppointmentDto;

@FeignClient(name = "AppointmentService", contextId = "appointmentService", path = "/smart-appointment/api/appointments")
public interface AppointmentClient {
    @PostMapping("/book")
    AppointmentDto createAppointment(@RequestBody AppointmentDto appointment);
    
    @GetMapping("/{appointmentId}")
    AppointmentDto getAppointment(@PathVariable Integer appointmentId);

    @GetMapping("/patient/{patientId}")
    List<AppointmentDto> getAppointmentsByPatientId(@PathVariable Integer patientId);

    @GetMapping("/doctor/{doctorId}")
    List<AppointmentDto> getAppointmentsDoctorIdWithOptionalParams(@PathVariable Integer doctorId, @RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end, @RequestParam(required = false) LocalDateTime now);
}
