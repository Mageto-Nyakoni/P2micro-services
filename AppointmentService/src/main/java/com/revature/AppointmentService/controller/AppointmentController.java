package com.revature.AppointmentService.controller;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.revature.AppointmentService.client.InfoClient;
import com.revature.AppointmentService.client.ScheduleClient;
import com.revature.AppointmentService.service.PatientService;
import com.revature.AppointmentService.dto.response.DoctorDto;
import com.revature.AppointmentService.dto.response.PatientDto;
import com.revature.AppointmentService.dto.response.SlotDto;
import com.revature.AppointmentService.dto.request.BookAppointmentRequestDto;
import com.revature.AppointmentService.dto.response.AppointmentDto;
import com.revature.AppointmentService.dto.response.DoctorAvailabilityDto;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.model.AppointmentType;
import com.revature.AppointmentService.repository.AppointmentRepository;
import com.revature.AppointmentService.service.AppointmentService;

@RestController
@RequestMapping("/smart-appointment/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
    private final AppointmentService appointmentService;
     private final InfoClient infoClient;
    private final ScheduleClient scheduleClient;


    public AppointmentController(AppointmentService appointmentService, InfoClient infoClient, ScheduleClient scheduleClient) {
        this.appointmentService = appointmentService;
        this.infoClient = infoClient;
        this.scheduleClient = scheduleClient;
    }


    // Book an appointment
    @PostMapping("/book")
    public ResponseEntity<AppointmentDto> bookAppointment(@RequestBody BookAppointmentRequestDto request) {

        // Validate patient exists
        PatientDto patient = infoClient.getPatient(request.getPatientId());
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }

        // Validate doctor exists
        DoctorDto doctor = infoClient.getDoctor(request.getDoctorId());
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }

        AppointmentDto appointment = appointmentService.bookAppointment(request);
        return ResponseEntity.ok(appointment);
    }

     //all appointment
    @GetMapping
    public List<AppointmentDto> getAllAppointments() {
        return appointmentService.getAll();
    }
   // cancel appointment
    @DeleteMapping("/cancel/{appointmentId}")
     public ResponseEntity<String> cancelAppointment(@PathVariable Integer appointmentId) {
        appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok("Appointment cancelled successfully");
    }
    


    // Get patient appointments
   public ResponseEntity<List<AppointmentDto>> getAppointmentsForPatient(@PathVariable Integer patientId) {
        PatientDto patient = infoClient.getPatient(patientId);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }

        List<AppointmentDto> appointments = appointmentService.getForPatient(patientId);
        return ResponseEntity.ok(appointments);
    }
    // ================= Get doctor availability =================
   @GetMapping("/availability")
    public Map<String, List<DoctorAvailabilityDto>> getAvailability() {
        Map<String, List<DoctorAvailabilityDto>> availabilityByDate = new HashMap<>();

        List<DoctorDto> doctors = infoClient.getAllDoctors();

        for (DoctorDto doctor : doctors) {
            List<SlotDto> slots = scheduleClient.getAvailableSlots(doctor.getDoctorId());

            Map<String, List<SlotDto>> slotsByDate = new HashMap<>();
            for (SlotDto slot : slots) {
                String dateKey = slot.getStartTime().substring(0, 10);
                slotsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(slot);
            }

            for (Map.Entry<String, List<SlotDto>> entry : slotsByDate.entrySet()) {
                DoctorAvailabilityDto doctorDto = new DoctorAvailabilityDto();
                doctorDto.setDoctorId(doctor.getDoctorId());
                doctorDto.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
                doctorDto.setSpecialization(doctor.getSpeciality());
                doctorDto.setSlots(entry.getValue());

                availabilityByDate.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(doctorDto);
            }
        }

        return availabilityByDate;
    }
}