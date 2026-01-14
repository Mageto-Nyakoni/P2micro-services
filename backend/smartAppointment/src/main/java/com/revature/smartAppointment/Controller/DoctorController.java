
package com.revature.smartAppointment.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.smartAppointment.Model.Doctor;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Service.DoctorService;
import com.revature.smartAppointment.Service.DoctorService.DoctorAppointmentView;
import com.revature.smartAppointment.Service.DoctorService.DoctorTimeSlotView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // -----------------------------
    // Basic CRUD for Doctor
    // -----------------------------

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

    // GET /doctors/{doctorId}
    @GetMapping("/{doctorId}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Integer doctorId) {
        return doctorService.findById(doctorId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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

    // GET /doctors/{doctorId}/appointments/today
    @GetMapping("/{doctorId}/appointments/today")
    public ResponseEntity<List<DoctorAppointmentView>> getTodaysAppointments(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(doctorService.getTodaysAppointments(doctorId));
    }

    // GET /doctors/{doctorId}/appointments/week
    @GetMapping("/{doctorId}/appointments/week")
    public ResponseEntity<List<DoctorAppointmentView>> getWeeksAppointments(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(doctorService.getCurrentWeeksAppointments(doctorId));
    }

    // GET /doctors/{doctorId}/appointments/{appointmentId}
    @GetMapping("/{doctorId}/appointments/{appointmentId}")
    public ResponseEntity<DoctorAppointmentView> getAppointmentDetails(@PathVariable Integer doctorId,
                                                                       @PathVariable Integer appointmentId) {
        return ResponseEntity.ok(doctorService.getAppointmentDetailsForDoctor(doctorId, appointmentId));
    }

    // -----------------------------
    // Actions: update status / cancel
    // -----------------------------

    // PATCH /doctors/{doctorId}/appointments/{appointmentId}/status
    @PatchMapping("/{doctorId}/appointments/{appointmentId}/status")
    public ResponseEntity<DoctorAppointmentView> updateStatus(@PathVariable Integer doctorId,
                                                              @PathVariable Integer appointmentId,
                                                              @RequestBody UpdateStatusRequest req) {
        return ResponseEntity.ok(doctorService.updateAppointmentStatus(doctorId, appointmentId, req.getStatus()));
    }

    // POST /doctors/{doctorId}/appointments/{appointmentId}/cancel
    @PostMapping("/{doctorId}/appointments/{appointmentId}/cancel")
    public ResponseEntity<DoctorAppointmentView> cancelAppointment(@PathVariable Integer doctorId,
                                                                   @PathVariable Integer appointmentId) {
        return ResponseEntity.ok(doctorService.cancelAppointment(doctorId, appointmentId));
    }

    // -----------------------------
    // Read-only: slots
    // -----------------------------

    // GET /doctors/{doctorId}/slots
    @GetMapping("/{doctorId}/slots")
    public ResponseEntity<List<DoctorTimeSlotView>> getSlots(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorTimeSlots(doctorId));
    }

    // -----------------------------
    // Request DTO
    // -----------------------------
    @Data
    @AllArgsConstructor
    public static class UpdateStatusRequest {
        private AppointmentStatus status;
        public UpdateStatusRequest() {}
    }
}
