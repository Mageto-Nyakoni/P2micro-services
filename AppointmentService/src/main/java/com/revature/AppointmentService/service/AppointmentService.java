package com.revature.AppointmentService.service;

import java.time.LocalDateTime;
import java.util.List;

import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.revature.AppointmentService.client.InfoClient;
import com.revature.AppointmentService.client.ScheduleClient;
import com.revature.AppointmentService.dto.response.AppointmentDto;
import com.revature.AppointmentService.dto.response.SlotDto;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.model.AppointmentStatus;
import com.revature.AppointmentService.repository.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository repo;
    private final InfoClient infoClient;
    private final ScheduleClient scheduleClient;

    public AppointmentService(
            AppointmentRepository repo,
            InfoClient infoClient,
            ScheduleClient scheduleClient
    ) {
        this.repo = repo;
        this.infoClient = infoClient;
        this.scheduleClient = scheduleClient;
    }
  

    // ================= BOOK APPOINTMENT =================
    public Appointment bookAppointment(
            Long patientId,
            Long doctorId,
            Long slotId,
            Long appointmentTypeId
    ) {

        // Validate Patient (Info Service)
        infoClient.getPatient(patientId);

        // Validate Doctor (Info Service)
        infoClient.getDoctor(doctorId);
// Validate Slot
    SlotDto slot = scheduleClient.getTimeSlot(slotId);
    if (!slot.isAvailable()) {
        throw new RuntimeException("Slot is not available");
    }
          // Convert slot start time from String to LocalDateTime
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME; // or match your string format
    LocalDateTime slotTime = LocalDateTime.parse(slot.getStartTime(), formatter);




          // Create Appointment
    Appointment appt = Appointment.builder()
            .patientId(patientId)
            .doctorId(doctorId)
            .slotId(slotId)
            .appointmentTypeId(appointmentTypeId)
            .dateTimeScheduled(slotTime)
            .status(AppointmentStatus.CONFIRMED)
            .build();

         // Save Appointment
    Appointment savedAppt = repo.save(appt);

    //  Mark slot as booked in ScheduleService
    scheduleClient.bookSlot(slotId);

    return savedAppt;
    }


   private AppointmentDto mapToDto(Appointment appt) {
    return new AppointmentDto(
            appt.getAppointmentId().intValue(),
            "Doctor " + appt.getDoctorId(),
            "Type " + appt.getAppointmentTypeId(),
            appt.getDateTimeScheduled(),
            appt.getDateTimeScheduled().plusMinutes(30),
            appt.getStatus()
    );
}
    // ================= GET DOCTOR APPOINTMENTS =================
    public List<AppointmentDto> getDoctorAppointments(Long doctorId) {
    return repo.findByDoctorId(doctorId)
            .stream()
            .map(this::mapToDto)
            .toList();
}

    // ================= GET PATIENT APPOINTMENTS =================
   public List<AppointmentDto> getPatientAppointments(Long patientId) {
    return repo.findByPatientId(patientId)
            .stream()
            .map(this::mapToDto)
            .toList();
}
    // ================= CANCEL APPOINTMENT =================
    public void cancelAppointment(Long id) {
        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus(AppointmentStatus.CANCELLED);
        repo.save(appt);
    }

    public Appointment getAppointmentById(Long id) {
    return repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
}





}
