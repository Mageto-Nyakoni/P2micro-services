package com.revature.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.revature.client.InfoClient;
import com.revature.client.ScheduleClient;
import com.revature.model.Appointment;
import com.revature.model.AppointmentStatus;
import com.revature.repository.AppointmentRepository;

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

        // Validate Slot (Scheduling Service)
        scheduleClient.getTimeSlot(slotId);

        // Create Appointment
        Appointment appt = Appointment.builder()
                .patientId(patientId)
                .doctorId(doctorId)
                .slotId(slotId)
                .appointmentTypeId(appointmentTypeId)
                .dateTimeScheduled(LocalDateTime.now()) // You can replace with slot time later
                .status(AppointmentStatus.CONFIRMED)
                .build();

        return repo.save(appt);
    }

    // ================= GET DOCTOR APPOINTMENTS =================
    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return repo.findByDoctorId(doctorId);
    }

    // ================= GET PATIENT APPOINTMENTS =================
    public List<Appointment> getPatientAppointments(Long patientId) {
        return repo.findByPatientId(patientId);
    }

    // ================= CANCEL APPOINTMENT =================
    public void cancelAppointment(Long id) {
        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus(AppointmentStatus.CANCELLED);
        repo.save(appt);
    }
}
