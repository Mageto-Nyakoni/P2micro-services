package com.revature.smartAppointment.Controller;
import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.AppointmentType;
import com.revature.smartAppointment.dto.AppointmentDto;
import com.revature.smartAppointment.dto.BookAppointmentRequestDto;

import java.util.List;
import com.revature.smartAppointment.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.AppointmentTypeRepository;


@RestController
@RequestMapping("/smart-appointment/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Book an appointment
    @PostMapping("/book")
    public ResponseEntity<AppointmentDto> bookAppointment(
             @RequestBody BookAppointmentRequestDto request)
             {
        AppointmentType type = appointmentTypeRepository.findById(request.typeId())
                .orElseThrow(() -> new RuntimeException("AppointmentType not found"));

        Appointment appointment = appointmentService.bookAppointment(request.slotId(), request.patientId(), type);
        AppointmentDto response = new AppointmentDto(
                    appointment.getAppointmentId(),
                    "Dr. " + appointment.getDoctor().getDoctorId(),
                    appointment.getAppointmentType().getName(),
                    appointment.getDateTimeScheduled(),
                    appointment.getStatus()
            );
         
        return ResponseEntity.ok(response);
    }

    // Get patient appointments
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDto>> getPatientAppointments(
            @PathVariable Integer patientId) {

        List<AppointmentDto> appointments =
                appointmentService.getAppointmentsForPatient(patientId);

        return ResponseEntity.ok(appointments);
    }
}
