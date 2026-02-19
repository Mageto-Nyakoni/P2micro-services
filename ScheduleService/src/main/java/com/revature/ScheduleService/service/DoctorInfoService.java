package com.revature.ScheduleService.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.revature.ScheduleService.client.DoctorInfoClient;

import feign.FeignException;

@Service
public class DoctorInfoService {

    private static final Logger log = LoggerFactory.getLogger(DoctorInfoService.class);

    private final DoctorInfoClient doctorInfoClient;

    public DoctorInfoService(DoctorInfoClient doctorInfoClient) {
        this.doctorInfoClient = doctorInfoClient;
    }

    public String getDoctorName(Integer doctorId) {
        if (doctorId == null) {
            return null;
        }

        try {
            Map<String, Object> payload = doctorInfoClient.getDoctor(doctorId);
            return extractDoctorName(payload);
        } catch (FeignException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Could not fetch doctor profile for doctorId={}", doctorId, ex);
            }
            return null;
        }
    }

    public boolean doctorExists(Integer doctorId) {
        if (doctorId == null) {
            return false;
        }

        try {
            return doctorInfoClient.getDoctor(doctorId) != null;
        } catch (FeignException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Doctor lookup failed for doctorId={}", doctorId, ex);
            }
            return false;
        }
    }

    private String extractDoctorName(Object payload) {
        if (!(payload instanceof Map<?, ?> map)) {
            return null;
        }

        String doctorName = asTrimmedString(map.get("doctorName"));
        if (doctorName != null) {
            return doctorName;
        }

        String fullName = joinName(
                asTrimmedString(map.get("firstName")),
                asTrimmedString(map.get("lastName"))
        );
        if (fullName != null) {
            return fullName;
        }

        String fromUser = extractDoctorName(map.get("user"));
        if (fromUser != null) {
            return fromUser;
        }

        return extractDoctorName(map.get("data"));
    }

    private String asTrimmedString(Object value) {
        if (!(value instanceof String text)) {
            return null;
        }
        String trimmed = text.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String joinName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
