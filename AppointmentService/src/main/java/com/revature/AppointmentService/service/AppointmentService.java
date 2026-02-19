package com.revature.AppointmentService.service;

import java.time.LocalDateTime;
import java.util.List;

import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import com.revature.AppointmentService.client.InfoClient;
import com.revature.AppointmentService.client.ScheduleClient;
import com.revature.AppointmentService.dto.response.AppointmentDto;
import com.revature.AppointmentService.dto.response.SlotDto;
import com.revature.AppointmentService.model.Appointment;
import com.revature.AppointmentService.model.AppointmentStatus;
import com.revature.AppointmentService.repository.AppointmentRepository;
import com.revature.AppointmentService.repository.AppointmentTypeRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository apptRepo;
    private final AppointmentTypeRepository typeRepo;
    private final InfoClient infoClient;
    private final ScheduleClient scheduleClient;

    public AppointmentService(AppointmentRepository apptRepo, AppointmentTypeRepository typeRepo, InfoClient infoClient, ScheduleClient scheduleClient) {
        this.apptRepo = apptRepo;
        this.infoClient = infoClient;
        this.typeRepo = typeRepo;
        this.scheduleClient = scheduleClient;
    }
  

    // ================= BOOK APPOINTMENT =================
    public Appointment bookAppointment(Integer patientId, Integer doctorId, Integer slotId, Integer appointmentTypeId) {
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
        Appointment appt = new Appointment(
            patientId,
            doctorId,
            slotId,
            typeRepo.findById(appointmentTypeId).get(),
            slotTime,
            AppointmentStatus.CONFIRMED
        );

         // Save Appointment
        Appointment savedAppt = apptRepo.save(appt);

        //  Mark slot as booked in ScheduleService
        scheduleClient.bookSlot(slotId);

        return savedAppt;
    }


   private AppointmentDto mapToDto(Appointment appt) {
    return new AppointmentDto(
        appt.getAppointmentId().intValue(),
        "Doctor " + appt.getDoctorId(),
        "Type " + appt.getAppointmentType().getName(),
        appt.getDateTimeScheduled(),
        appt.getDateTimeScheduled().plusMinutes(30),
        appt.getStatus()
    );
}
    // ================= GET DOCTOR APPOINTMENTS =================
    public List<AppointmentDto> getDoctorAppointments(Integer doctorId) {
        return apptRepo.findByDoctorId(doctorId)
            .stream()
            .map(this::mapToDto)
            .toList();
    }

    // ================= GET PATIENT APPOINTMENTS =================
    public List<AppointmentDto> getPatientAppointments(Integer patientId) {
        return apptRepo.findByPatientId(patientId)
            .stream()
            .map(this::mapToDto)
            .toList();
    }
    // ================= CANCEL APPOINTMENT =================
    public void cancelAppointment(Integer id) {
        Appointment appt = apptRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus(AppointmentStatus.CANCELLED);
        apptRepo.save(appt);
    }

    public Appointment getAppointmentById(Integer id) {
        return apptRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }


    public List<AppointmentDto> getDoctorAppointmentBetweenStartAndEnd(Integer doctorId, LocalDateTime start, LocalDateTime end) {
        return apptRepo.findByDoctorIdAndDateTimeScheduledBetween(doctorId, start, end)
            .stream()
            .map(this::mapToDto)
            .toList();
    }


    public List<AppointmentDto> getDoctorAppointmentAfterNow(Integer doctorId, LocalDateTime now) {
        return apptRepo.findByDoctorIdAndDateTimeScheduledAfter(doctorId, now)
            .stream()
            .map(this::mapToDto)
            .toList();
    }
}
