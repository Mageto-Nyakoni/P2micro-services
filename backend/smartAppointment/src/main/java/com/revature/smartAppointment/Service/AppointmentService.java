package com.revature.smartAppointment.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.TimeSlotRepository;

@Service
public class AppointmentService implements ServiceInterface<Appointment> {
   
      private final AppointmentRepository appointmentRepository;
      private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, TimeSlotRepository timeSlotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.timeSlotRepository = timeSlotRepository;
    }


    @Override
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
            // update fields as needed
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
        TimeSlot slot = appointment.getSlot(); // Appointment must have a reference to TimeSlot
        if (slot != null) {
            slot.setStatus(TimeSlotStatus.AVAILABLE); // mark as available
            timeSlotRepository.save(slot);
        }

        // Mark appointment as cancelled
        appointment.setStatus(AppointmentStatus.CANCELLED); // make sure Appointment has boolean 'cancelled'
        appointmentRepository.save(appointment);
    }
}