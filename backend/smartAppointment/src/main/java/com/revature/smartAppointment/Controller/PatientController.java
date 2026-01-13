package com.revature.smartAppointment.Controller;

import com.revature.smartAppointment.Model.Patient;
import com.revature.smartAppointment.Model.User;
import com.revature.smartAppointment.Service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/smart-appointment/api/patient")
@CrossOrigin(origins = "http://localhost:4200")
public class PatientController {
    private PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping()
    public ResponseEntity<List<Patient>> getPatients() {
        return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/{patient_id}")
    public ResponseEntity<Patient> getPatient(@PathVariable int patient_id) {
        Optional<Patient> optionalPatient = patientService.findById(patient_id);
        if (optionalPatient.isPresent()) {
            return ResponseEntity.status(200).body(optionalPatient.get());
        }
        return ResponseEntity.status(400).build();
    }
}
