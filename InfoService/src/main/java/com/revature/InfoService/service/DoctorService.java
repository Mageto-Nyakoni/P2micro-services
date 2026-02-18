package com.revature.InfoService.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.revature.InfoService.client.AppointmentClient;
import com.revature.InfoService.client.TimeSlotClient;
import com.revature.InfoService.client.UserClient;
import com.revature.InfoService.dto.AppointmentDto;
import com.revature.InfoService.dto.AppointmentDoctorView;
import com.revature.InfoService.dto.Slot;
import com.revature.InfoService.dto.TimeSlot;
import com.revature.InfoService.dto.TimeSlotDoctorView;
import com.revature.InfoService.dto.User;
import com.revature.InfoService.dto.request.DoctorInfoRequest;
import com.revature.InfoService.model.Doctor;
import com.revature.InfoService.repository.DoctorRepository;

@Service
public class DoctorService implements ServiceInterface<Doctor> {
    private final DoctorRepository doctorRepository;
    private final SpecialityService specialityService;
    private final PatientService patientService;
    private final UserClient userClient;
    private final TimeSlotClient timeSlotClient;
    private final AppointmentClient appointmentClient;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository, SpecialityService specialityService, PatientService patientService, UserClient userClient, TimeSlotClient timeSlotClient, AppointmentClient appointmentClient) {
        this.doctorRepository = doctorRepository;
        this.specialityService = specialityService;
        this.patientService = patientService;
        this.userClient = userClient;
        this.timeSlotClient = timeSlotClient;
        this.appointmentClient = appointmentClient;
    }
     
    @Override
    @Transactional
    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
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
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);
        if (optionalDoctor.isPresent()) {
            doctorRepository.deleteById(id);
        }
        return optionalDoctor;
    }

    @Override
    public Doctor updateById(int id, Doctor newDoctor) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            if (newDoctor.getExperienceYears() != null) doctor.setExperienceYears(newDoctor.getExperienceYears());
            if (newDoctor.getGender() != null) doctor.setGender(newDoctor.getGender());
            if (newDoctor.getSpeciality() != null) doctor.setSpeciality(newDoctor.getSpeciality());
            if (newDoctor.getBio() != null) doctor.setBio(newDoctor.getBio());

            return doctorRepository.save(doctor);
        }
        return null;
    }

    public Optional<Doctor> findByUserId(int user_id) {
        return doctorRepository.findDoctorByUserId(user_id);
    }
    
    public List<AppointmentDoctorView> getUpcomingAppointments(Integer doctorId) {
        ensureDoctorExists(doctorId);

        LocalDateTime now = LocalDateTime.now();

        List<AppointmentDto> appts = appointmentClient.findBySlotDoctorDoctorIdAndDateTimeScheduledAfter(doctorId, now);

        return appts.stream()
            .map(this::toAppointmentDoctorView)
            .toList();
    }

    public List<AppointmentDoctorView> getAllAppointmentsForDoctor(Integer doctorId) {
        ensureDoctorExists(doctorId);
        List<AppointmentDto> appointments = appointmentClient.findBySlotDoctorDoctorIdOrderByDateTimeScheduledAsc(doctorId);
        return appointments.stream()
            .map(this::toAppointmentDoctorViewWithPatient)
            .toList();
    }

    private AppointmentDoctorView toAppointmentDoctorViewWithPatient(AppointmentDto a) {
        String firstName = null;
        String lastName = null;
        String appointmentType = null;
        Integer estimatedTime = 0;

        if (a.getPatientId() != null) {
            User user = userClient.getUser(patientService.findById(a.getPatientId().intValue()).get().getUserId());
            firstName = user.getFirstName();
            lastName = user.getLastName();
        }

        if (a.getAppointmentTypeId() != null) {
            appointmentType = appointmentClient.getAppointmentTypeById(a.getAppointmentTypeId()).getName(); // if enum
            estimatedTime = appointmentClient.getAppointmentTypeById(a.getAppointmentTypeId()).getEstimatedTime();
        }

        return new AppointmentDoctorView(
            a.getAppointmentId().intValue(),
            firstName,
            lastName,
            appointmentType,
            a.getDateTimeScheduled(),
            estimatedTime,
            a.getStatus()
        );
    }



    public List<AppointmentDoctorView> getTodaysAppointments(Integer doctorId) {
        ensureDoctorExists(doctorId);
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // Requires repo method:
        // List<Appointment> findBySlotDoctorDoctorIdAndDateTimeScheduledBetween(Integer doctorId, LocalDateTime start, LocalDateTime end);
        List<AppointmentDto> appts = appointmentClient.findBySlotDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start, end);

        return appts.stream().map(this::toAppointmentDoctorView).toList();
    }

    public List<AppointmentDoctorView> getCurrentWeeksAppointments(Integer doctorId) {
        ensureDoctorExists(doctorId);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime start = weekStart.atStartOfDay();
        LocalDateTime end = weekEnd.atTime(LocalTime.MAX);

        List<AppointmentDto> appts = appointmentClient.findBySlotDoctorDoctorIdAndDateTimeScheduledBetween(doctorId, start, end);

        return appts.stream().map(this::toAppointmentDoctorView).toList();
    }

    public AppointmentDoctorView getAppointmentDetailsForDoctor(Integer doctorId, Long appointmentId) {
        AppointmentDto appt = appointmentClient.getAppointment(appointmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        enforceOwnership(doctorId, appt);
        return toAppointmentDoctorView(appt);
    }

    @Transactional
    public AppointmentDoctorView updateAppointmentStatus(Integer doctorId, Long appointmentId, String newStatus) {
        // Proposal: doctor can update status completed/cancelled/no-show
        // :contentReference[oaicite:7]{index=7}

        AppointmentDto appt = appointmentClient.getAppointment(appointmentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        enforceOwnership(doctorId, appt);

        appt.setStatus(newStatus);

        AppointmentDto saved = appointmentClient.createAppointment(appt);

        TimeSlot slot = timeSlotClient.getTimeSlot(appt.getSlotId());
        if (slot != null) {
            if (newStatus.equals("CANCELLED") || newStatus.equals("DENIED")) {
                slot.setStatus("AVAILABLE"); // free the slot
            } else {
                slot.setStatus("BOOKED"); // keep slot booked (COMPLETED, NO_SHOW, etc.)
            }
            timeSlotClient.updateSlotStatus(slot.getSlotId(), slot.getStatus());
        }

        return toAppointmentDoctorView(saved);
    }

    @Transactional
    public AppointmentDoctorView cancelAppointment(Integer doctorId, Long appointmentId) {
        // Proposal: doctor can cancel patient appointment
        // :contentReference[oaicite:8]{index=8}

        return updateAppointmentStatus(doctorId, appointmentId, "CANCELLED");
    }

    // Read-only: Doctor time slots
    public List<TimeSlotDoctorView> getDoctorTimeSlots(Integer doctorId) {
        ensureDoctorExists(doctorId);
        // Proposal: doctor can view time slot details, but cannot create/delete/modify
        // :contentReference[oaicite:9]{index=9}

        List<Slot> slots = timeSlotClient.getDoctorSlotsByDate(doctorId, "");

        return slots.stream()
            .map(s -> new TimeSlotDoctorView(
                s.getSlotId(),
                LocalTime.parse(s.getStartTime()),
                LocalTime.parse(s.getEndTime())))
            .toList();
    }

    // Helpers / DTO mapping

    private void ensureDoctorExists(Integer doctorId) {
        doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }

    private void enforceOwnership(Integer doctorId, AppointmentDto appt) {
        if (appt.getSlotId() == null || timeSlotClient.getTimeSlot(appt.getSlotId()).getDoctor() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Appointment has no assigned doctor"
            );
        }

        Integer apptDoctorId = timeSlotClient.getTimeSlot(appt.getSlotId()).getDoctor().getDoctorId();

        if (!apptDoctorId.equals(doctorId)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "This appointment does not belong to the doctor"
            );
        }
    }

    private AppointmentDoctorView toAppointmentDoctorView(AppointmentDto a) {
        String patientFirstName = null;
        String patientLastName = null;
        if (a.getPatientId() != null) {
            User user = userClient.getUser(patientService.findById(a.getPatientId().intValue()).get().getUserId());
            patientFirstName = user.getFirstName();
            patientLastName = user.getLastName();
        }
        // Calculate duration from slot start/end time if slot exists
        Integer estimatedDurationMinutes = null;
        if (a.getSlotId() != null && timeSlotClient.getTimeSlot(a.getSlotId()).getStartTime() != null && timeSlotClient.getTimeSlot(a.getSlotId()).getEndTime() != null) {
            estimatedDurationMinutes = (int) java.time.Duration
                .between(timeSlotClient.getTimeSlot(a.getSlotId()).getStartTime(), timeSlotClient.getTimeSlot(a.getSlotId()).getEndTime())
                .toMinutes();
        }

        return new AppointmentDoctorView(
            a.getAppointmentId().intValue(),
            patientFirstName,
            patientLastName,
            appointmentClient.getAppointmentTypeById(a.getAppointmentTypeId()).getName(), // placeholder for appointment type
            a.getDateTimeScheduled(),
            estimatedDurationMinutes,
            a.getStatus()
        );
    }
    
    public Doctor convertRequestToObject(DoctorInfoRequest info) {
        Doctor doctor = new Doctor();

        doctor.setExperienceYears(info.getExperience());
        doctor.setBio(info.getBio());
        doctor.setGender(info.getGender());
        doctor.setSpeciality(specialityService.findSpecialityBySpecialityName(info.getSpeciality()).get());
        
        return doctor;
    }
}