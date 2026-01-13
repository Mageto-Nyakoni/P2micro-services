package com.revature.smartAppointment.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.revature.smartAppointment.Model.*;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.DoctorRepository;
import com.revature.smartAppointment.Repository.TimeSlotRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    // -----------------------------
    // Doctor Dashboard: Appointments
    // -----------------------------

    public List<DoctorAppointmentView> getTodaysAppointments(Integer doctorId) {
        ensureDoctorExists(doctorId);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // Requires repo method:
        // List<Appointment> findByDoctorDoctorIdAndDateTimeScheduledBetween(Integer doctorId, LocalDateTime start, LocalDateTime end);
        List<Appointment> appts =
                appointmentRepository.findByDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start, end);

        return appts.stream().map(DoctorService::toDoctorAppointmentView).toList();
    }

    public List<DoctorAppointmentView> getCurrentWeeksAppointments(Integer doctorId) {
        ensureDoctorExists(doctorId);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekEnd.atTime(LocalTime.MAX);

        List<Appointment> appts =
                appointmentRepository.findByDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start, end);

        return appts.stream().map(DoctorService::toDoctorAppointmentView).toList();
    }

    public DoctorAppointmentView getAppointmentDetailsForDoctor(Integer doctorId, Integer appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        enforceOwnership(doctorId, appt);
        return toDoctorAppointmentView(appt);
    }

    // ---------------------------------
    // Doctor Actions: Status Management
    // ---------------------------------

    @Transactional
    public DoctorAppointmentView updateAppointmentStatus(Integer doctorId, Integer appointmentId, AppointmentStatus newStatus) {
        // Proposal: doctor can update status completed/cancelled/no-show
        // :contentReference[oaicite:7]{index=7}

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        enforceOwnership(doctorId, appt);

        appt.setStatus(newStatus);
        Appointment saved = appointmentRepository.save(appt);

        return toDoctorAppointmentView(saved);
    }

    @Transactional
    public DoctorAppointmentView cancelAppointment(Integer doctorId, Integer appointmentId) {
        // Proposal: doctor can cancel patient appointment
        // :contentReference[oaicite:8]{index=8}

        return updateAppointmentStatus(doctorId, appointmentId, AppointmentStatus.CANCELLED);
    }

    // -----------------------------
    // Read-only: Doctor time slots
    // -----------------------------

    public List<DoctorTimeSlotView> getDoctorTimeSlots(Integer doctorId) {
        ensureDoctorExists(doctorId);

        // Proposal: doctor can view time slot details, but cannot create/delete/modify
        // :contentReference[oaicite:9]{index=9}

        // Requires repo method:
        // List<TimeSlot> findByDoctorDoctorId(Integer doctorId);
        List<TimeSlot> slots = timeSlotRepository.findByDoctorDoctorId(doctorId);

        return slots.stream()
                .map(s -> new DoctorTimeSlotView(
                        s.getSlotId(),
                        s.getStartTime(),
                        s.getEndTime()
                ))
                .toList();
    }

    // -----------------------------
    // Helpers / DTO mapping
    // -----------------------------

    private void ensureDoctorExists(Integer doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }

    private void enforceOwnership(Integer doctorId, Appointment appt) {
        if (appt.getDoctor() == null || appt.getDoctor().getDoctorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment has no assigned doctor");
        }
        if (!appt.getDoctor() == null || appt.getDoctor().getDoctorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment has no assigned doctor");
        }
        if (!appt.getDoctor().getDoctorId().equals(doctorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This appointment does not belong to the doctor");
        }
    }

    private static DoctorAppointmentView toDoctorAppointmentView(Appointment a) {
        String patientFirst = (a.getPatient() != null) ? a.getPatient().getFirstName() : null;
        String patientLast = (a.getPatient() != null) ? a.getPatient().getLastName() : null;

        String apptTypeName = (a.getAppointmentType() != null) ? a.getAppointmentType().getName() : null;
        Integer estMinutes = (a.getAppointmentType() != null) ? a.getAppointmentType().getEstimatedTime() : null;

        // "Basic patient info" is intentionally light here to avoid leaking sensitive data by default
        // :contentReference[oaicite:10]{index=10}
        return new DoctorAppointmentView(
                a.getAppointmentId(),
                patientFirst,
                patientLast,
                apptTypeName,
                a.getDateTimeScheduled(),
                estMinutes,
                a.getStatus()
        );
    }

    // -----------------------------
    // Lightweight response DTOs
    // -----------------------------

    @Data
    @AllArgsConstructor
    public static class DoctorAppointmentView {
        private Integer appointmentId;

        private String patientFirstName;
        private String patientLastName;

        private String appointmentType;
        private LocalDateTime scheduledDateTime;
        private Integer estimatedDurationMinutes;

        private AppointmentStatus status;
    }

    @Data
    @AllArgsConstructor
    public static class DoctorTimeSlotView {
        private Integer slotId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
