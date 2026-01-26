package com.revature.smartAppointment.ServiceTest;

import com.revature.smartAppointment.Model.Appointment;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Repository.AppointmentRepository;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
import com.revature.smartAppointment.Service.admin.AdminAppointmentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private AdminAppointmentService adminAppointmentService;

    private Appointment appointment;

    @BeforeEach
    void setup() {
        appointment = new Appointment();
        appointment.setAppointmentId(1);
        appointment.setStatus(AppointmentStatus.REQUESTED);
        appointment.setDateTimeScheduled(LocalDateTime.of(2026, 1, 25, 10, 0));
    }

    // =====================
    // getAllAppointments
    // =====================
    @Test
    void getAllAppointments_returnsAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        List<Appointment> result = adminAppointmentService.getAllAppointments();

        assertEquals(1, result.size());
        assertEquals(appointment, result.get(0));
        verify(appointmentRepository).findAll();
    }

    // =====================
    // updateStatus
    // =====================
    @Test
    void updateStatus_existingAppointment_updatesStatus() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment updated = adminAppointmentService.updateStatus(1, AppointmentStatus.CONFIRMED);

        assertEquals(AppointmentStatus.CONFIRMED, updated.getStatus());
        verify(appointmentRepository).findById(1);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void updateStatus_nonExistingAppointment_throwsException() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminAppointmentService.updateStatus(1, AppointmentStatus.CONFIRMED));

        assertEquals("Appointment not found", ex.getMessage());
        verify(appointmentRepository).findById(1);
        verify(appointmentRepository, never()).save(any());
    }

    // =====================
    // reschedule
    // =====================
    @Test
    void reschedule_existingAppointment_updatesDateTime() {
        LocalDateTime newDateTime = LocalDateTime.of(2026, 1, 26, 11, 30);
        when(appointmentRepository.findById(1)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment rescheduled = adminAppointmentService.reschedule(1, newDateTime);

        assertEquals(newDateTime, rescheduled.getDateTimeScheduled());
        verify(appointmentRepository).findById(1);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void reschedule_nonExistingAppointment_throwsException() {
        when(appointmentRepository.findById(1)).thenReturn(Optional.empty());

        LocalDateTime newDateTime = LocalDateTime.of(2026, 1, 26, 11, 30);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminAppointmentService.reschedule(1, newDateTime));

        assertEquals("Appointment not found", ex.getMessage());
        verify(appointmentRepository).findById(1);
        verify(appointmentRepository, never()).save(any());
    }
}
