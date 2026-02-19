package com.revature.AppointmentService.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.AppointmentService.client.InfoClient;
import com.revature.AppointmentService.dto.response.PatientDto;


    @Service
    public class PatientService {

        private final InfoClient infoClient;

        @Autowired
        public PatientService(InfoClient infoClient) {
            this.infoClient = infoClient;
        }

       public Optional<PatientDto> findByUserId(Integer userId) {
        try {
            PatientDto patient = (PatientDto) infoClient.getPatient(userId);
            return Optional.ofNullable(patient);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

