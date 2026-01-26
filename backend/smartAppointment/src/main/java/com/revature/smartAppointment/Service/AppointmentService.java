package com.revature.smartAppointment.Service;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.revature.smartAppointment.Controller.PatientAppointmentController;
import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.AppointmentType;
import com.revature.smartAppointment.Model.Patient;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.AppointmentTypeRepository;
import com.revature.smartAppointment.Repository.PatientRepository;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
import com.revature.smartAppointment.dto.AppointmentDto;

@Service
public class AppointmentService implements ServiceInterface<Appointment> {
    private final AppointmentTypeRepository appointmentTypeRepository;
    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, TimeSlotRepository timeSlotRepository, AppointmentTypeRepository appointmentTypeRepository) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.appointmentTypeRepository = appointmentTypeRepository;
    }

    @Override
    @Transactional
    public Appointment save(Appointment entity) {
        return appointmentRepository.save(entity);
    }

    @Override
    public Optional<Appointment> findById(int id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Optional<Appointment> deleteById(int id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        appointment.ifPresent(a -> appointmentRepository.delete(a));
        return appointment;
    }

    @Override
    public Appointment updateById(int id, Appointment entity) {
        Optional<Appointment> existingAppointment = appointmentRepository.findById(id);
        if (existingAppointment.isPresent()) {
            Appointment toUpdate = existingAppointment.get();
            toUpdate.setDoctor(entity.getDoctor());
            toUpdate.setSlot(entity.getSlot());
            toUpdate.setAppointmentType(entity.getAppointmentType());
            toUpdate.setPatient(entity.getPatient());
            toUpdate.setDateTimeScheduled(entity.getDateTimeScheduled());
            toUpdate.setStatus(entity.getStatus());
            return appointmentRepository.save(toUpdate);
        }
        return null;
    }

    @Transactional
    public void cancelAppointment(int appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Free the slot
        TimeSlot slot = appointment.getSlot();
        if (slot != null) {
            slot.setStatus(TimeSlotStatus.AVAILABLE);
            timeSlotRepository.save(slot);
        }

        // Cancel the appointment
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    // Get all appointments for a patient
    public List<AppointmentDto> getAppointmentsForPatient(Integer patientId) {
    List<Appointment> appointments = appointmentRepository.findByPatient_PatientId(patientId);

    return appointments.stream()
            .map(appt -> new AppointmentDto(
                    appt.getAppointmentId(),
                    "Dr. " + appt.getDoctor().getDoctorId(), // ideally use getDoctor().getUser().getFirstName() + getLastName()
                    appt.getAppointmentType().getName(),
                    LocalDateTime.of(appt.getSlot().getDateAvailable(), appt.getSlot().getStartTime()),
                    appt.getStatus()
            ))
            .collect(Collectors.toList()); // <-- changed here
}

 // New method: get only CONFIRMED appointments
    public List<Appointment> getConfirmedAppointmentsForPatient(Integer patientId) {
        return appointmentRepository.findByPatient_PatientIdAndStatus(patientId, AppointmentStatus.CONFIRMED);
    }


    @Autowired
private PatientRepository patientRepository;
    @Transactional
public Appointment bookAppointment(Integer slotId, Integer patientId, AppointmentType appointmentType) {
    // Fetch the slot
    TimeSlot slot = timeSlotRepository.findById(slotId)
            .orElseThrow(() -> new RuntimeException("TimeSlot not found"));

    // Check if slot is available
    if (slot.getStatus() != TimeSlotStatus.AVAILABLE) {
        throw new RuntimeException("TimeSlot is not available");
    }

    // Fetch the patient
    Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

    // Create the appointment
    /*Appointment appointment = new Appointment();
    appointment.setSlot(slot);
    appointment.setDoctor(slot.getDoctor());
    appointment.setPatient(patient);
    appointment.setAppointmentType(appointmentType);
    appointment.setDateTimeScheduled(java.time.LocalDateTime.of(slot.getDateAvailable(), slot.getStartTime()));
    appointment.setStatus(AppointmentStatus.CONFIRMED);
    appointment.setCreatedAt(java.time.LocalDateTime.now());*/


   Appointment appointment = Appointment.builder()
           .doctor(slot.getDoctor())
            .slot(slot)
            .appointmentType(appointmentType)
            .patient(patient)
            .dateTimeScheduled(LocalDateTime.of(slot.getDateAvailable(), slot.getStartTime()))
            .status(AppointmentStatus.CONFIRMED)
            .build();
            
    // Mark the slot as booked **after creating the appointment**
    slot.setStatus(TimeSlotStatus.BOOKED);
    

    // Save and return the appointment
    timeSlotRepository.save(slot);
    return appointmentRepository.save(appointment);
}

@Transactional
public Appointment createAppointment(Appointment appointment) {
    if (appointment.getStatus() == null) {
        appointment.setStatus(AppointmentStatus.CONFIRMED);
    }
    return appointmentRepository.save(appointment);
}

 @Transactional
    public Map<String, Object> createAppointmentFromSlot(Patient patient, Integer slotId, Integer appointmentTypeId) {
        AppointmentType type = appointmentTypeRepository.findById(appointmentTypeId)
                .orElseThrow(() -> new RuntimeException("AppointmentType not found"));

        Appointment appointment = bookAppointment(slotId, patient.getPatientId(), type);

        Map<String, Object> result = new HashMap<>();
        result.put("appointmentId", appointment.getAppointmentId());
        result.put("slotId", appointment.getSlot().getSlotId());
        result.put("status", appointment.getStatus());
        return result;
    }

 // --- Cancel appointment specifically for a patient ---
    @Transactional
    public void cancelAppointmentForPatient(Integer patientId, Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getPatientId().equals(patientId)) {
            throw new RuntimeException("Patient does not own this appointment");
        }

        cancelAppointment(appointmentId);
    }
}
