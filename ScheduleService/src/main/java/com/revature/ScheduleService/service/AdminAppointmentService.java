package com.revature.ScheduleService.service;

import com.revature.ScheduleService.model.enums.AppointmentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminAppointmentService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String appointmentServiceBaseUrl;
    private final String adminAppointmentsPath;
    private final String usersTablePath;

    public AdminAppointmentService(
            @Value("${appointment.service.base-url:http://localhost:8083}") String appointmentServiceBaseUrl,
            @Value("${appointment.service.admin-appointments-path:/smart-appointment/api/admin/appointments}") String adminAppointmentsPath,
            @Value("${appointment.service.users-table-path:/smart-appointment/api/users/table}") String usersTablePath
    ) {
        this.appointmentServiceBaseUrl = appointmentServiceBaseUrl;
        this.adminAppointmentsPath = adminAppointmentsPath;
        this.usersTablePath = usersTablePath;
    }

    public List<Map<String, Object>> getAppointments(String authHeader, String status) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseAppointmentsUrl());
        if (status != null && !status.isBlank()) {
            builder.queryParam("status", status);
        }

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    builder.build(true).toUri(),
                    HttpMethod.GET,
                    authEntity(authHeader),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        }
    }

    public Map<String, Object> updateStatus(String authHeader, Integer id, AppointmentStatus status) {
        HttpMethod method;
        String actionPath;

        if (status == AppointmentStatus.CONFIRMED) {
            method = HttpMethod.PUT;
            actionPath = "/accept";
        } else if (status == AppointmentStatus.CANCELLED) {
            method = HttpMethod.PUT;
            actionPath = "/cancel";
        } else if (status == AppointmentStatus.DENIED) {
            method = HttpMethod.PATCH;
            actionPath = "/deny";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported status");
        }

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    UriComponentsBuilder
                            .fromUriString(baseAppointmentsUrl() + "/" + id + actionPath)
                            .build(true)
                            .toUri(),
                    method,
                    authEntity(authHeader),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody() : new HashMap<>();
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        }
    }

    public Map<String, Object> reschedule(String authHeader, Integer appointmentId, LocalDateTime newDateTime) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    UriComponentsBuilder
                            .fromUriString(baseAppointmentsUrl() + "/" + appointmentId + "/reschedule")
                            .queryParam("date", newDateTime.toLocalDate())
                            .queryParam("time", newDateTime.toLocalTime())
                            .build(true)
                            .toUri(),
                    HttpMethod.PUT,
                    authEntity(authHeader),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody() : new HashMap<>();
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        }
    }

    public List<Map<String, Object>> getStaff(String authHeader) {
        String url = joinUrl(appointmentServiceBaseUrl, usersTablePath);
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    UriComponentsBuilder.fromUriString(url).build(true).toUri(),
                    HttpMethod.GET,
                    authEntity(authHeader),
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody() == null ? List.of() : response.getBody();
        } catch (RestClientResponseException ex) {
            throw mapClientException(ex);
        }
    }

    private String baseAppointmentsUrl() {
        if (appointmentServiceBaseUrl.endsWith("/") && adminAppointmentsPath.startsWith("/")) {
            return appointmentServiceBaseUrl.substring(0, appointmentServiceBaseUrl.length() - 1) + adminAppointmentsPath;
        }
        if (!appointmentServiceBaseUrl.endsWith("/") && !adminAppointmentsPath.startsWith("/")) {
            return appointmentServiceBaseUrl + "/" + adminAppointmentsPath;
        }
        return appointmentServiceBaseUrl + adminAppointmentsPath;
    }

    private ResponseStatusException mapClientException(RestClientResponseException ex) {
        HttpStatusCode statusCode;
        try {
            statusCode = ex.getStatusCode();
        } catch (IllegalArgumentException ignore) {
            statusCode = HttpStatus.BAD_GATEWAY;
        }
        String message = ex.getResponseBodyAsString();
        if (message == null || message.isBlank()) {
            message = "Appointment service request failed";
        }
        return new ResponseStatusException(statusCode, message, ex);
    }

    private HttpEntity<?> authEntity(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null && !authHeader.isBlank()) {
            headers.set("Authorization", authHeader);
        }
        return new HttpEntity<>(headers);
    }

    private String joinUrl(String base, String path) {
        if (base.endsWith("/") && path.startsWith("/")) {
            return base.substring(0, base.length() - 1) + path;
        }
        if (!base.endsWith("/") && !path.startsWith("/")) {
            return base + "/" + path;
        }
        return base + path;
    }
}
