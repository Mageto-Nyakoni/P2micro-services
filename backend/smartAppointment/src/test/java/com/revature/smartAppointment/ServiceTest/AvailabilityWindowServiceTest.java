package com.revature.smartAppointment.ServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revature.smartAppointment.Model.*;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Repository.*;
import com.revature.smartAppointment.Service.AvailabilityWindowService;
import com.revature.smartAppointment.dto.AvailabilityWindowDTO;

@ExtendWith(MockitoExtension.class)
class AvailabilityWindowServiceTest {

    @Mock
    private AvailabilityWindowRepository windowRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AvailabilityWindowService availabilityWindowService;

    // =====================
    // createWindow
    // =====================

    @Test
    void createWindow_success_createsWindowAndSlots() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1);

        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(10, 0);

        when(doctorRepository.findById(1))
            .thenReturn(Optional.of(doctor));
        when(timeSlotRepository.existsByDoctorAndDateAvailableAndStartTime(
                any(), any(), any()))
            .thenReturn(false);

        AvailabilityWindow window =
            availabilityWindowService.createWindow(1, date, start, end);

        assertNotNull(window);
        assertEquals(doctor, window.getDoctor());
        assertTrue(window.isActive());

        verify(windowRepository).save(any(AvailabilityWindow.class));
        verify(timeSlotRepository, atLeastOnce()).save(any(TimeSlot.class));
    }

    @Test
    void createWindow_doctorNotFound_throwsException() {
        when(doctorRepository.findById(1))
            .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> availabilityWindowService.createWindow(
                1,
                LocalDate.now(),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0))
        );

        assertEquals("Doctor not found", ex.getMessage());
        verifyNoInteractions(windowRepository, timeSlotRepository);
    }

    // =====================
    // generateSlots (indirectly)
    // =====================

    @Test
    void createWindow_doesNotDuplicateSlots() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1);

        when(doctorRepository.findById(1))
            .thenReturn(Optional.of(doctor));

        // Slot already exists
        when(timeSlotRepository.existsByDoctorAndDateAvailableAndStartTime(
                any(), any(), any()))
            .thenReturn(true);

        availabilityWindowService.createWindow(
            1,
            LocalDate.now(),
            LocalTime.of(9, 0),
            LocalTime.of(10, 0)
        );

        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    // =====================
    // getWindowsForDoctor
    // =====================

    @Test
    void getWindowsForDoctor_success_mapsToDTOs() {
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");

        Doctor doctor = new Doctor();
        doctor.setDoctorId(1);
        doctor.setUser(user);

        AvailabilityWindow window = AvailabilityWindow.builder()
                .windowId(100)
                .doctor(doctor)
                .date(LocalDate.now())
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(12, 0))
                .active(true)
                .build();

        when(windowRepository.findActiveWindowsByDoctorIdWithDoctor(1))
            .thenReturn(List.of(window));

        List<AvailabilityWindowDTO> result =
            availabilityWindowService.getWindowsForDoctor(1);

        assertEquals(1, result.size());
        AvailabilityWindowDTO dto = result.get(0);

        assertEquals(100, dto.getWindowId());
        assertEquals(1, dto.getDoctorId());
        assertEquals("Jane Doe", dto.getDoctorName());
        assertTrue(dto.isActive());
    }

    @Test
    void getWindowsForDoctor_noWindows_returnsEmptyList() {
        when(windowRepository.findActiveWindowsByDoctorIdWithDoctor(1))
            .thenReturn(List.of());

        List<AvailabilityWindowDTO> result =
            availabilityWindowService.getWindowsForDoctor(1);

        assertTrue(result.isEmpty());
    }
}