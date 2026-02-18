package com.revature.InfoService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.revature.InfoService.dto.Appointment;

@FeignClient(name = "AppointmentService", contextId = "appointmentService", path = "/smart-appointment/api/appointments")
public interface AppointmentClient {
    @GetMapping("/{appointmentId}")
    Appointment getAppointment(@PathVariable Long appointmentId);
}
