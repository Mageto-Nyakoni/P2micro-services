package com.revature.InfoService.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.InfoService.dto.Appointment;
import com.revature.InfoService.dto.AppointmentPatientView;
import com.revature.InfoService.dto.request.PatientInfoRequest;
import com.revature.InfoService.model.Patient;
import com.revature.InfoService.service.PatientService;

@RestController
@RequestMapping("/smart-appointment/api/patients")
@CrossOrigin("*")
public class PatientController {
    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    
    @GetMapping("{patientId}/appointments")
    public List<AppointmentPatientView> getPatientAppointments(@PathVariable Integer patientId) {
        List<Appointment> appointments = findByPatient_PatientIdAndStatus(patientId, AppointmentStatus.CONFIRMED);

        return appointments.stream().map(appt -> new AppointmentPatientView(
            appt.getAppointmentId(),
            appt.getDoctor().getUser().getFirstName() + " " + appt.getDoctor().getUser().getLastName(),
            appt.getAppointmentType().getName(),
            appt.getDateTimeScheduled(),
            appt.getDateTimeScheduled().plusMinutes(30),
            appt.getStatus()
        )).collect(Collectors.toList());
    }

    @GetMapping()
    public ResponseEntity<List<Patient>> getPatients() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<Patient> getPatient(@RequestHeader("Authorization") String authHeader, @PathVariable int user_id) {
        try {
            /*
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            String privilege = jwtUtil.extractPrivilege(token);
            if ((privilege.equals("Patient") && jwtUtil.extractId(token) == user_id) || privilege.equals("Doctor")) {
                Optional<Patient> optionalPatient = patientService.findByUserId(user_id);
                if (optionalPatient.isPresent()) {
                    return ResponseEntity.ok(optionalPatient.get());
                }
                return ResponseEntity.status(400).build();
            } else {
                throw new RuntimeException("Invalid token");
            }
            */
            return null;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<Patient> getMyPatient(@RequestHeader("Authorization") String authHeader) {
        try {
            /*
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            String privilege = jwtUtil.extractPrivilege(token);
            if (!privilege .equals("Patient")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            int userId = jwtUtil.extractId(token);

            return patientService.findByUserId(userId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
            */
            return null;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<Patient> updatePatient(@RequestHeader("Authorization") String authHeader, @PathVariable int user_id, @RequestBody PatientInfoRequest patientInfo) {
        try {
            /*
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            String privilege = jwtUtil.extractPrivilege(token);
            if (privilege.equals("Patient") && jwtUtil.extractId(token) == user_id) {

                int patient_id = patientService.findByUserId(user_id).get().getPatientId();

                Patient newPatient = patientService.convertRequestToObject(patientInfo);

                Patient patient = patientService.updateById(patient_id, newPatient);

                return ResponseEntity.ok(patient);

            } else {
                throw new RuntimeException("Unauthorized access");
            }
            */
            return null;
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
}