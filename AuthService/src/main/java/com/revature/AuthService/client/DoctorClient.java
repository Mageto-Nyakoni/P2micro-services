package com.revature.AuthService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.revature.AuthService.model.Doctor;

@FeignClient(name = "InfoService", contextId = "doctorClient", path = "/smart-appointment/api/doctors")
public interface DoctorClient {
    @PostMapping
    Doctor createDoctor(@RequestBody Doctor doctor);

    @GetMapping("/user/{userId}")
    Doctor getDoctorByUserId(@PathVariable Integer userId);

    @DeleteMapping("/{doctorId}")
    ResponseEntity<?> deleteDoctor(@PathVariable Integer doctorId);
}
