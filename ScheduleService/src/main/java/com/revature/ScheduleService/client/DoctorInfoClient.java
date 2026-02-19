package com.revature.ScheduleService.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "${clients.doctor.service-name:InfoService}",
        contextId = "doctorInfoClient",
        path = "${clients.doctor.path:/smart-appointment/api/doctors}"
)
public interface DoctorInfoClient {

    @GetMapping("/{doctorId}")
    Map<String, Object> getDoctor(@PathVariable("doctorId") Integer doctorId);
}
