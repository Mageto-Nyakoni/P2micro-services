package com.revature.smartAppointment.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.Doctor;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.DoctorRepository;
import com.revature.smartAppointment.Repository.TimeSlotRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService implements ServiceInterface<Doctor> {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    // Basic CRUD for Doctor

    @Override
    public Doctor save(Doctor entity) {
        return doctorRepository.save(entity);
    }

    @Override
    public Optional<Doctor> findById(int id) {
        return doctorRepository.findById(id);
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public Optional<Doctor> deleteById(int id) {
        Optional<Doctor> existing = doctorRepository.findById(id);
        existing.ifPresent(d -> doctorRepository.deleteById(id));
        return existing;
    }

    @Override
    public Doctor updateById(int id, Doctor updated) {
        Optional<Doctor> existingOpt = doctorRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return null;
        }

        Doctor existing = existingOpt.get();
        // Only core doctor fields are updated; relationships (user, specialty) come
        // from the payload.
        existing.setUser(updated.getUser());
        existing.setExperienceYears(updated.getExperienceYears());
        existing.setGender(updated.getGender());
        existing.setSpecialty(updated.getSpecialty());
        existing.setBio(updated.getBio());

        return doctorRepository.save(existing);
    }

    // Doctor Dashboard: Appointments


    public List<DoctorAppointmentView> getTodaysAppointments(Integer doctorId) {
        ensureDoctorExists(doctorId);

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // Requires repo method:
        // List<Appointment> findByDoctorDoctorIdAndDateTimeScheduledBetween(Integer
        // doctorId, LocalDateTime start, LocalDateTime end);
        List<Appointment> appts = appointmentRepository.findByDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start,
                end);

        return appts.stream().map(DoctorService::toDoctorAppointmentView).toList();
    }

    public List<DoctorAppointmentView> getCurrentWeeksAppointments(Integer doctorId) {
        ensureDoctorExists(doctorId);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekEnd.atTime(LocalTime.MAX);

        List<Appointment> appts = appointmentRepository.findByDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start,
                end);

        return appts.stream().map(DoctorService::toDoctorAppointmentView).toList();
    }

    public DoctorAppointmentView getAppointmentDetailsForDoctor(Integer doctorId, Integer appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        enforceOwnership(doctorId, appt);
        return toDoctorAppointmentView(appt);
    }

    // Doctor Actions: Status Management

    @Transactional
    public DoctorAppointmentView updateAppointmentStatus(Integer doctorId, Integer appointmentId,
            AppointmentStatus newStatus) {
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

    // Read-only: Doctor time slots

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
                        s.getEndTime()))
                .toList();
    }

    // Helpers / DTO mapping

    private void ensureDoctorExists(Integer doctorId) {
        doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }

    /**
     * NOTE:
     * The current {@link Appointment} entity does not maintain a relationship to
     * {@link Doctor},
     * so we cannot reliably enforce ownership of an appointment by a specific
     * doctor.
     *
     * This method is intentionally left as a no-op to avoid runtime errors while
     * still keeping
     * the extension point for future ownership checks if/when the data model is
     * expanded.
     */
    private void enforceOwnership(Integer doctorId, Appointment appt) {
        // Ownership enforcement not implemented with current data model.
    }

    private static DoctorAppointmentView toDoctorAppointmentView(Appointment a) {
        return new DoctorAppointmentView(
                a.getAppointmentId(),
                null, // patientFirstName (not ready with Appointment model currently, wip)
                null, // patientLastName (not ready with Appointment model currently, wip)
                null, // appointmentType (not ready with Appointment model currently, wip)
                a.getDateTimeScheduled(),
                null, // estimatedDurationMinutes (not ready with Appointment model currently, wip)
                a.getStatus());
    }

    // Lightweight response DTOs

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
