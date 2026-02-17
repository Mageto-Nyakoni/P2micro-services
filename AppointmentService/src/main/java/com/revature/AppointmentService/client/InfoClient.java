package com.revature.AppointmentService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// Does the patient exist & Does the doctor exist - infoservice
@FeignClient(name = "info-service")
public interface InfoClient {

    @GetMapping("/doctors/{id}")
    Object getDoctor(@PathVariable("id") Long id);

    @GetMapping("/patients/{id}")
    Object getPatient(@PathVariable("id") Long id);
}
