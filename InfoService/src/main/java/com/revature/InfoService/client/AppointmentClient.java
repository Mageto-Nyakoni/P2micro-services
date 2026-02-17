package com.revature.InfoService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "AppointmentService", contextId = "appointmentService", path = "/smart-appointment/api/appointments")
public interface AppointmentClient {
    @GetMapping("/{appointmentId")
    
}
