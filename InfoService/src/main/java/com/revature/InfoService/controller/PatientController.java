package com.revature.InfoService.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.InfoService.client.AppointmentClient;
import com.revature.InfoService.client.AuthClient;
import com.revature.InfoService.client.UserClient;
import com.revature.InfoService.dto.AppointmentDto;
import com.revature.InfoService.dto.AppointmentPatientView;
import com.revature.InfoService.dto.User;
import com.revature.InfoService.dto.request.PatientInfoRequest;
import com.revature.InfoService.dto.response.AuthResponse;
import com.revature.InfoService.model.Patient;
import com.revature.InfoService.service.DoctorService;
import com.revature.InfoService.service.PatientService;

@RestController
@RequestMapping("/smart-appointment/api/patients")
@CrossOrigin("*")
public class PatientController {
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AuthClient authClient;
    private final UserClient userClient;
    private final AppointmentClient appointmentClient;

    @Autowired
    public PatientController(PatientService patientService, DoctorService doctorService, AuthClient authClient, UserClient userClient, AppointmentClient appointmentClient) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.authClient = authClient;
        this.userClient = userClient;
        this.appointmentClient = appointmentClient;
    }
    
    @GetMapping
    public ResponseEntity<List<Patient>> getPatients() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.save(patient));
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<Patient> getPatient(@RequestHeader("Authorization") String authHeader, @PathVariable int user_id) {
        try {
            AuthResponse authResponse = authClient.validateToken(authHeader);

            if (!authResponse.getIsValid()) {
                throw new RuntimeException("Invalid token");
            }

            String privilege = authResponse.getPrivilege();
            if ((privilege.equals("Patient") && authResponse.getUserId() == user_id) || privilege.equals("Doctor")) {
                Optional<Patient> optionalPatient = patientService.findByUserId(user_id);
                if (optionalPatient.isPresent()) {
                    return ResponseEntity.ok(optionalPatient.get());
                }
                return ResponseEntity.status(400).build();
            } else {
                throw new RuntimeException("Invalid token: Privilege found was " + privilege + " and user id was " + authResponse.getUserId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<Patient> getMyPatient(@RequestHeader("Authorization") String authHeader) {
        try {
            AuthResponse authResponse = authClient.validateToken(authHeader);

            if (!authResponse.getIsValid()) {
                throw new RuntimeException("Invalid token");
            }
            
            String privilege = authResponse.getPrivilege();
            if (!privilege .equals("Patient")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            int userId = authResponse.getUserId();

            return patientService.findByUserId(userId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<Patient> updatePatient(@RequestHeader("Authorization") String authHeader, @PathVariable int user_id, @RequestBody PatientInfoRequest patientInfo) {
        try {
            AuthResponse authResponse = authClient.validateToken(authHeader);

            if (!authResponse.getIsValid()) {
                throw new RuntimeException("Invalid token");
            }
            
            String privilege = authResponse.getPrivilege();
            if (privilege.equals("Patient") && authResponse.getUserId() == user_id) {
                int patient_id = patientService.findByUserId(user_id).get().getPatientId();
                Patient newPatient = patientService.convertRequestToObject(patientInfo);
                Patient patient = patientService.updateById(patient_id, newPatient);

                return ResponseEntity.ok(patient);

            } else {
                throw new RuntimeException("Unauthorized access");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/{patient_id}")
    public ResponseEntity<?> deletePatient(@RequestHeader("Authorization") String authHeader, @PathVariable(name="patient_id") int patientId) {
        return patientService.deleteById(patientId)
            .map(p -> ResponseEntity.noContent().build())
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{patientId}/appointments")
    public List<AppointmentPatientView> getPatientAppointments(@PathVariable Integer patientId) {
        List<AppointmentDto> appointmentDtos = appointmentClient.getAppointmentsByPatientId(patientId);

        return appointmentDtos.stream().map(appt -> {
            User user = userClient.getUser(doctorService.findById(appt.getDoctorId().intValue()).get().getUserId());
            return new AppointmentPatientView(
                appt.getAppointmentId().intValue(),
                user.getFirstName() + " " + user.getLastName(),
                appt.getAppointmentType().getName(),
                appt.getDateTimeScheduled(),
                appt.getDateTimeScheduled().plusMinutes(30),
                appt.getStatus()
            );  
        }).collect(Collectors.toList());
    } 
}