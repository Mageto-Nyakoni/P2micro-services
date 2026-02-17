package com.revature.AuthService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.revature.AuthService.model.Patient;

@FeignClient(name = "InfoService", contextId = "patientClient", path = "/smart-appointment/api/patient")
public interface PatientClient {
    @PostMapping
    Patient createPatient(@RequestBody Patient patient);

    @GetMapping("/{userId}")
    Patient getPatient(@PathVariable Integer userId);

    @DeleteMapping("/{patientId}")
    ResponseEntity<?> deletePatient(@PathVariable Integer patientId);
}