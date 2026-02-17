package com.revature.Controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.revature.Model.TimeSlot;
import com.revature.Model.enums.AppointmentStatus;
import com.revature.Model.enums.TimeSlotStatus;
import com.revature.Service.TimeSlotService;
import com.revature.Service.AdminAppointmentService;
import com.revature.Service.AdminScheduleService;
import com.revature.Service.AuthValidationClient;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("smart-appointment/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final AdminAppointmentService adminAppointmentService;
    private final AdminScheduleService adminScheduleService;
    private final TimeSlotService timeSlotService;
    private final AuthValidationClient authValidationClient;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String appointmentServiceBaseUrl;
    private final String usersTablePath;

    @Autowired
    public AdminController(
            AdminAppointmentService adminAppointmentService,
            AdminScheduleService adminScheduleService,
            TimeSlotService timeSlotService,
            AuthValidationClient authValidationClient,
            @Value("${appointment.service.base-url:http://localhost:8083}") String appointmentServiceBaseUrl,
            @Value("${appointment.service.users-table-path:/smart-appointment/api/users/table}") String usersTablePath
    ) {
        this.adminAppointmentService = adminAppointmentService;
        this.adminScheduleService = adminScheduleService;
        this.timeSlotService = timeSlotService;
        this.authValidationClient = authValidationClient;
        this.appointmentServiceBaseUrl = appointmentServiceBaseUrl;
        this.usersTablePath = usersTablePath;
    }

    //  View ALL appointments (system-wide)
    @GetMapping("/appointments")
    public ResponseEntity<List<Map<String, Object>>> getAllAppointments(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(adminAppointmentService.getAppointments(authHeader, status));
    }

    //  Accept appointment
    @PutMapping("/appointments/{id}/accept")
    public ResponseEntity<Map<String, Object>> accept(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(authHeader, id, AppointmentStatus.CONFIRMED)
        );
    }

    //  Cancel appointment
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancel(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(authHeader, id, AppointmentStatus.CANCELLED)
        );
    }

    //  Deny appointment
    @PatchMapping("/appointments/{id}/deny")
    public ResponseEntity<Map<String, Object>> deny(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
                adminAppointmentService.updateStatus(authHeader, id, AppointmentStatus.DENIED)
        );
    }

    //  Reschedule appointment
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<Map<String, Object>> reschedule(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer id,
            @RequestParam LocalDate date,
            @RequestParam LocalTime time
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(
            adminAppointmentService.reschedule(
                    authHeader,
                    id,
                    java.time.LocalDateTime.of(date, time)
            )
        );
    }

    @PostMapping("/doctors/{doctorId}/schedule")
    public TimeSlot addSchedule(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer doctorId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime start,
            @RequestParam LocalTime end
    ) {
        validateAdmin(authHeader);
        return adminScheduleService.addDoctorAvailability(doctorId, date, start, end);
    }

    @GetMapping("/doctors/{doctorId}/time-slots")
    public ResponseEntity<List<TimeSlotService.PublicTimeSlotView>> getDoctorSlots(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer doctorId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(timeSlotService.getSlotsForDoctorRange(doctorId, from, to));
    }

    @GetMapping("/time-slots")
    public ResponseEntity<List<TimeSlotService.PublicTimeSlotView>> getAllTimeSlots(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) TimeSlotStatus status,
            @RequestParam(required = false) Integer doctorId
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(timeSlotService.getAdminTimeSlots(doctorId, from, to, status));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<Map<String, Object>>> getStaff(
            @RequestHeader("Authorization") String authHeader
    ) {
        validateAdmin(authHeader);
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(joinUrl(appointmentServiceBaseUrl, usersTablePath))
                    .build(true)
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    uri,
                    org.springframework.http.HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            return ResponseEntity.ok(response.getBody() == null ? List.of() : response.getBody());
        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.valueOf(ex.getRawStatusCode()), ex.getResponseBodyAsString(), ex);
        }
    }

    private void validateAdmin(String authHeader) {
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        AuthValidationClient.AuthValidationResult validation = authValidationClient.validate(authHeader);
        if (!validation.valid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        String privilege = validation.privilege();
        if (!"Admin".equals(privilege) && !"Super".equals(privilege)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
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
