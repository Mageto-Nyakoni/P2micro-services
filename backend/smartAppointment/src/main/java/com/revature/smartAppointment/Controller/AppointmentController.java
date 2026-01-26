package com.revature.smartAppointment.Controller;
import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.AppointmentType;
import com.revature.smartAppointment.Model.Doctor;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.dto.AppointmentDto;
import com.revature.smartAppointment.dto.BookAppointmentRequestDto;
import com.revature.smartAppointment.dto.DoctorAvailabilityDto;
import com.revature.smartAppointment.dto.SlotDto;

import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.revature.smartAppointment.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.AppointmentTypeRepository;
import com.revature.smartAppointment.Repository.DoctorRepository;


@RestController
@RequestMapping("/smart-appointment/api/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;
     
    @Autowired
    private AppointmentRepository appointmentRepository;
     @Autowired
      private DoctorRepository doctorRepository;


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
 @GetMapping("/patients/{patientId}/appointments")
public List<AppointmentDto> getPatientAppointments(@PathVariable Integer patientId) {
    List<Appointment> appointments = appointmentRepository.findByPatientPatientId(patientId);

    return appointments.stream()
            .map(app -> new AppointmentDto(
                    app.getAppointmentId(),
                    app.getDoctor().getUser().getFirstName() + " " + app.getDoctor().getUser().getLastName(),
                    app.getAppointmentType().getName(),
                    app.getDateTimeScheduled(),
                    app.getStatus()
            ))
            .collect(Collectors.toList());
}
// ================= Get doctor availability =================
    @GetMapping("/availability")
    public Map<String, List<DoctorAvailabilityDto>> getAvailability() {
        
        Map<String, List<DoctorAvailabilityDto>> availabilityByDate = new HashMap<>();

        List<Doctor> doctors = doctorRepository.findAll();

        for (Doctor doctor : doctors) {

             Map<String, List<SlotDto>> slotsByDate = new HashMap<>();
            

            for (TimeSlot slot : doctor.getSlots()) {
            if (slot.getStatus() != TimeSlotStatus.AVAILABLE) continue;

            String dateKey = slot.getDateAvailable().toString();

            SlotDto slotDto = new SlotDto();
            slotDto.setSlotId(slot.getSlotId());
            slotDto.setStartTime(slot.getStartTime().toString());
            slotDto.setEndTime(slot.getEndTime().toString());
            slotDto.setAvailable(true);

            slotsByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(slotDto);
        }

        // Now add doctor per date
        for (Map.Entry<String, List<SlotDto>> entry : slotsByDate.entrySet()) {
            String dateKey = entry.getKey();
            List<SlotDto> slots = entry.getValue();

            DoctorAvailabilityDto doctorDto = new DoctorAvailabilityDto();
            doctorDto.setDoctorId(doctor.getDoctorId());
            doctorDto.setDoctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
            doctorDto.setSpecialization(doctor.getSpeciality().getSpecialityName());
            doctorDto.setSlots(slots);

            availabilityByDate.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(doctorDto);
        }
    }

    return availabilityByDate;
}
}