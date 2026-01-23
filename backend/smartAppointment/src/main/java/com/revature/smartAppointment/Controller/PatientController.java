package com.revature.smartAppointment.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.smartAppointment.Controller.Request.PatientInfoRequest;
import com.revature.smartAppointment.Model.Patient;
import com.revature.smartAppointment.Service.PatientService;
import com.revature.smartAppointment.Util.JwtUtil;

@RestController
@RequestMapping("/smart-appointment/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {
    private PatientService patientService;
    private JwtUtil jwtUtil;

    @Autowired
    public PatientController(PatientService patientService, JwtUtil jwtUtil) {
        this.patientService = patientService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping()
    public ResponseEntity<List<Patient>> getPatients() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<Patient> getPatient(@RequestHeader("Authorization") String authHeader, @PathVariable int user_id) {
        try {
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

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<Patient> updatePatient(@RequestHeader("Authorization") String authHeader, @PathVariable int user_id, @RequestBody PatientInfoRequest patientInfo) {
        try {
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}