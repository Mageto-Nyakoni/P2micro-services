package com.revature.Service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class DoctorInfoClient {

    private static final Logger log = LoggerFactory.getLogger(DoctorInfoClient.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String doctorServiceBaseUrl;
    private final String doctorByIdPath;

    public DoctorInfoClient(
            @Value("${doctor.service.base-url:http://localhost:8082}") String doctorServiceBaseUrl,
            @Value("${doctor.service.by-id-path:/smart-appointment/api/doctors/{doctorId}}") String doctorByIdPath
    ) {
        this.doctorServiceBaseUrl = doctorServiceBaseUrl;
        this.doctorByIdPath = doctorByIdPath;
    }

    public String getDoctorName(Integer doctorId) {
        if (doctorId == null) {
            return null;
        }

        try {
            Object payload = fetchDoctorPayload(doctorId);
            return extractDoctorName(payload);
        } catch (RestClientException ex) {
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
            return fetchDoctorPayload(doctorId) != null;
        } catch (RestClientException ex) {
            if (log.isDebugEnabled()) {
                log.debug("Doctor lookup failed for doctorId={}", doctorId, ex);
            }
            return false;
        }
    }

    private Object fetchDoctorPayload(Integer doctorId) {
        String url = buildUrl(doctorServiceBaseUrl, doctorByIdPath);
        return restTemplate.getForObject(url, Object.class, doctorId);
    }

    private String buildUrl(String baseUrl, String path) {
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + path;
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
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
