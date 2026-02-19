package com.revature.InfoService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.InfoService.client.AuthClient;
import com.revature.InfoService.dto.AppointmentDoctorView;
import com.revature.InfoService.dto.TimeSlotDoctorView;
import com.revature.InfoService.dto.request.DoctorInfoRequest;
import com.revature.InfoService.dto.request.UpdateStatusRequest;
import com.revature.InfoService.dto.response.AuthResponse;
import com.revature.InfoService.model.Doctor;
import com.revature.InfoService.service.DoctorService;

@RestController
@RequestMapping("/smart-appointment/api/doctors")
@CrossOrigin("*")
public class DoctorController {
    private final DoctorService doctorService;
    private final AuthClient authClient;


    @Autowired
    public DoctorController(DoctorService doctorService, AuthClient authClient) {
        this.doctorService = doctorService;
        this.authClient = authClient;
    }

    // Basic CRUD for Doctor

    // POST /doctors
    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.save(doctor));
    }

    // GET /doctors
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<Doctor> getDoctorByUserId(@PathVariable Integer userId) {
        return doctorService.findByUserId(userId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /doctors/{doctorId}
    @GetMapping("/{doctorId}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Integer doctorId) {
        return doctorService.findById(doctorId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /doctors/me
    @GetMapping("/me")
    public ResponseEntity<Doctor> getMyDoctor(@RequestHeader("Authorization") String authHeader) {
        try {
            AuthResponse authResponse = authClient.validateToken(authHeader);

            if (!authResponse.getIsValid()) {
                throw new RuntimeException("Invalid token");
            }

            String privilege = authResponse.getPrivilege();
            if (!privilege.equals("Doctor")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            int userId = authResponse.getUserId();

            return doctorService.findByUserId(userId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // PUT /doctors/{doctorId}
    @PutMapping("/{doctorId}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Integer doctorId, @RequestBody Doctor doctor) {
        Doctor updated = doctorService.updateById(doctorId, doctor);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{user_id}")
    public ResponseEntity<Doctor> updateDoctor(@RequestHeader("Authorization") String authHeader, @PathVariable int user_id, @RequestBody DoctorInfoRequest doctorInfo) {
         try {
            AuthResponse authResponse = authClient.validateToken(authHeader);

            if (!authResponse.getIsValid()) {
                throw new RuntimeException("Invalid token");
            }

            String privilege = authResponse.getPrivilege();
            if ((privilege.equals("Doctor") && authResponse.getUserId() == user_id) || privilege.equals("Super")) {
                int doctor_id = doctorService.findByUserId(user_id).get().getDoctorId();
                Doctor newDoctor = doctorService.convertRequestToObject(doctorInfo);
                Doctor doctor = doctorService.updateById(doctor_id, newDoctor);

                return ResponseEntity.ok(doctor);

            } else {
                throw new RuntimeException("Unauthorized access");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // DELETE /doctors/{doctorId}
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Integer doctorId) {
        return doctorService.deleteById(doctorId)
            .map(d -> ResponseEntity.noContent().build())
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -----------------------------
    // Dashboard: appointments today/week
    // -----------------------------


    // GET /doctors/me/appointments/today
    @GetMapping("/me/appointments/today")
    public ResponseEntity<List<AppointmentDoctorView>> getMyTodaysAppointments(@RequestHeader("Authorization") String authHeader) {
        AuthResponse authResponse = authClient.validateToken(authHeader);

        if (!authResponse.getIsValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!authResponse.getPrivilege().equals("Doctor")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int userId = authResponse.getUserId();
        int doctorId = doctorService.findByUserId(userId)
            .orElseThrow()
            .getDoctorId();

        return ResponseEntity.ok(
            doctorService.getTodaysAppointments(doctorId)
        );
    }
    
   @GetMapping("/{doctorId}/appointments/upcoming")
    public List<AppointmentDoctorView> getUpcomingAppointments(
            @PathVariable Integer doctorId) {

        return doctorService.getUpcomingAppointments(doctorId);
    }

    @GetMapping("/{doctorId}/appointments/all")
    public ResponseEntity<List<AppointmentDoctorView>> getAllAppointments(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(doctorService.getAllAppointmentsForDoctor(doctorId));
    }

    // GET /doctors/{doctorId}/appointments/today
    @GetMapping("/{doctorId}/appointments/today")
    public ResponseEntity<List<AppointmentDoctorView>> getTodaysAppointments(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(doctorService.getTodaysAppointments(doctorId));
    }

    // GET /doctors/{doctorId}/appointments/week
    @GetMapping("/{doctorId}/appointments/week")
    public ResponseEntity<List<AppointmentDoctorView>> getWeeksAppointments(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(doctorService.getCurrentWeeksAppointments(doctorId));
    }

    // GET /doctors/{doctorId}/appointments/{appointmentId}
    @GetMapping("/{doctorId}/appointments/{appointmentId}")
    public ResponseEntity<AppointmentDoctorView> getAppointmentDetails(@PathVariable Integer doctorId, @PathVariable Integer appointmentId) {
        return ResponseEntity.ok(doctorService.getAppointmentDetailsForDoctor(doctorId, appointmentId));
    }

    // -----------------------------
    // Actions: update status / cancel
    // -----------------------------

    // PATCH /doctors/{doctorId}/appointments/{appointmentId}/status
    @PatchMapping("/{doctorId}/appointments/{appointmentId}/status")
    public ResponseEntity<AppointmentDoctorView> updateStatus(@PathVariable Integer doctorId, @PathVariable Integer appointmentId, @RequestBody UpdateStatusRequest req) {
        System.out.println("Received status: " + req.getStatus());                                         
        return ResponseEntity.ok(doctorService.updateAppointmentStatus(doctorId, appointmentId, req.getStatus()));
    }

    // POST /doctors/{doctorId}/appointments/{appointmentId}/cancel
    @PostMapping("/{doctorId}/appointments/{appointmentId}/cancel")
    public ResponseEntity<AppointmentDoctorView> cancelAppointment(@PathVariable Integer doctorId, @PathVariable Integer appointmentId) {
        return ResponseEntity.ok(doctorService.cancelAppointment(doctorId, appointmentId));
    }

    // -----------------------------
    // Read-only: slots
    // -----------------------------

    // GET /doctors/{doctorId}/slots
    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<List<TimeSlotDoctorView>> getSlots(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorTimeSlots(doctorId));
    }
}