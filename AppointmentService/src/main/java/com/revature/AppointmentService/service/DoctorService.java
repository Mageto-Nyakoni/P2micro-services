package com.revature.AppointmentService.service;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.AppointmentService.client.InfoClient;
import com.revature.AppointmentService.dto.response.DoctorDto;

@Service
public class DoctorService {

    private final InfoClient infoClient;

    @Autowired
    public DoctorService(InfoClient infoClient) {
        this.infoClient = infoClient;
    }

    public Optional<DoctorDto> findById(Integer userId) {
        try {
            DoctorDto doctor = (DoctorDto) infoClient.getDoctor(userId);
            return Optional.ofNullable(doctor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
}
