package com.revature.InfoService.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
}
