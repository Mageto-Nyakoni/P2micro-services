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
import com.revature.InfoService.dto.AppointmentType;

@FeignClient(name = "AppointmentService", contextId = "appointmentService", path = "/smart-appointment/api")
public interface AppointmentClient {
    @GetMapping("/appointments/{appointmentId}")
    AppointmentDto getAppointment(@PathVariable Long appointmentId);

    @GetMapping("/appointments/patients/{patientId}")
    List<AppointmentDto> getAppointmentsByPatientId(@PathVariable Long patientId);

    @GetMapping("/appointments/doctor/{doctorId}")
    List<AppointmentDto> getAppointmentsDoctorId(@PathVariable Long doctorId);

    @GetMapping("/appointment-types/{appointmentTypeId}")
    AppointmentType getAppointmentTypeById(@PathVariable Long appointmentTypeId);

    @GetMapping("/appointments")
    List<AppointmentDto> findByDoctorIdAndStartEndTimeBetween(@RequestParam(required = false) Integer doctorId, @RequestParam(required = false) LocalDateTime start, @RequestParam(required = false) LocalDateTime end);

    @PostMapping("/appointments")
    AppointmentDto createAppointment(@RequestBody AppointmentDto appointment);

    @GetMapping("/appointments/doctor/{doctorId}")
    List<AppointmentDto> findByDoctorIdAndDateTimeScheduledAfter(@PathVariable Integer doctorId,@RequestParam LocalDateTime now);
}
