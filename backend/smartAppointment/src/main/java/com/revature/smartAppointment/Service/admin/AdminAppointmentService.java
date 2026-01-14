package com.revature.smartAppointment.Service.admin;

import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TimeSlotRepository timeSlotRepository;

    //  View all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    //  Update appointment status
    public Appointment updateStatus(Integer id, AppointmentStatus status) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appt.setStatus(status);
        return appointmentRepository.save(appt);
    }

    //  Reschedule appointment using a TimeSlot
    public Appointment reschedule(Integer appointmentId, Integer newSlotId) {

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        TimeSlot slot = timeSlotRepository.findById(newSlotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        //  Update flat fields
        appt.setSlotId(slot.getSlotId());
        appt.setDateTimeScheduled(slot.getStartTime());
        appt.setStatus(AppointmentStatus.CONFIRMED);

        return appointmentRepository.save(appt);
    }
}
