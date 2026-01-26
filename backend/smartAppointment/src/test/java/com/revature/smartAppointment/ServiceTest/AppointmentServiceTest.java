package com.revature.smartAppointment.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
import com.revature.smartAppointment.Service.AppointmentService;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    // =====================
    // save
    // =====================

    @Test
    void save_success() {
        Appointment appointment = new Appointment();

        when(appointmentRepository.save(appointment))
            .thenReturn(appointment);

        Appointment saved = appointmentService.save(appointment);

        assertNotNull(saved);
        verify(appointmentRepository).save(appointment);
    }

    // =====================
    // findById
    // =====================

    @Test
    void findById_found() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);

        when(appointmentRepository.findById(1))
            .thenReturn(Optional.of(appointment));

        Optional<Appointment> result = appointmentService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getAppointmentId());
    }

    @Test
    void findById_notFound() {
        when(appointmentRepository.findById(1))
            .thenReturn(Optional.empty());

        Optional<Appointment> result = appointmentService.findById(1);

        assertTrue(result.isEmpty());
    }

    // =====================
    // findAll
    // =====================

    @Test
    void findAll_success() {
        when(appointmentRepository.findAll())
            .thenReturn(List.of(new Appointment(), new Appointment()));

        List<Appointment> result = appointmentService.findAll();

        assertEquals(2, result.size());
    }

    // =====================
    // deleteById
    // =====================

    @Test
    void deleteById_found_deletes() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);

        when(appointmentRepository.findById(1))
            .thenReturn(Optional.of(appointment));

        Optional<Appointment> result =
            appointmentService.deleteById(1);

        assertTrue(result.isPresent());
        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void deleteById_notFound() {
        when(appointmentRepository.findById(1))
            .thenReturn(Optional.empty());

        Optional<Appointment> result =
            appointmentService.deleteById(1);

        assertTrue(result.isEmpty());
        verify(appointmentRepository, never()).delete(any());
    }

    // =====================
    // updateById
    // =====================

    @Test
    void updateById_success() {
        Appointment existing = new Appointment();
        existing.setAppointmentId(1);

        Appointment updates = new Appointment();
        updates.setStatus(AppointmentStatus.COMPLETED);

        when(appointmentRepository.findById(1))
            .thenReturn(Optional.of(existing));
        when(appointmentRepository.save(existing))
            .thenReturn(existing);

        Appointment result =
            appointmentService.updateById(1, updates);

        assertNotNull(result);
        assertEquals(AppointmentStatus.COMPLETED, result.getStatus());
        verify(appointmentRepository).save(existing);
    }

    @Test
    void updateById_notFound() {
        when(appointmentRepository.findById(1))
            .thenReturn(Optional.empty());

        Appointment result =
            appointmentService.updateById(1, new Appointment());

        assertNull(result);
        verify(appointmentRepository, never()).save(any());
    }

    // =====================
    // cancelAppointment
    // =====================

    @Test
    void cancelAppointment_success_freesSlotAndCancels() {
        TimeSlot slot = new TimeSlot();
        slot.setStatus(TimeSlotStatus.BOOKED);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setSlot(slot);
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        when(appointmentRepository.findById(1))
            .thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(1);

        assertEquals(TimeSlotStatus.AVAILABLE, slot.getStatus());
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());

        verify(timeSlotRepository).save(slot);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancelAppointment_noSlot_stillCancelsAppointment() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setSlot(null);

        when(appointmentRepository.findById(1))
            .thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(1);

        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
        verify(timeSlotRepository, never()).save(any());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancelAppointment_notFound_throwsException() {
        when(appointmentRepository.findById(1))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> appointmentService.cancelAppointment(1)
        );

        assertEquals("Appointment not found", ex.getMessage());
    }
}