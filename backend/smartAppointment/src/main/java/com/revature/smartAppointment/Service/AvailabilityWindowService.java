package com.revature.smartAppointment.Service;

import com.revature.smartAppointment.Model.*;
import com.revature.smartAppointment.Repository.*;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AvailabilityWindowService {

    private final AvailabilityWindowRepository windowRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public AvailabilityWindow createWindow(
            Integer doctorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        AvailabilityWindow window = AvailabilityWindow.builder()
                .doctor(doctor)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .active(true)
                .build();

        windowRepository.save(window);

        generateSlots(window);

        return window;
    }

    private void generateSlots(AvailabilityWindow window) {

        LocalTime current = window.getStartTime();

        while (current.plusMinutes(30).isAfter(window.getEndTime())) {

            LocalTime slotEnd = current.plusMinutes(30);

            boolean exists = timeSlotRepository
                    .existsByDoctorAndDateAvailableAndStartTime(
                            window.getDoctor(),
                            window.getDate(),
                            current
                    );

            if (!exists) {
                TimeSlot slot = new TimeSlot(
                        current,
                        slotEnd,
                        window.getDate(),
                        window.getDoctor()
                );

                slot.setStatus(TimeSlotStatus.AVAILABLE);
                timeSlotRepository.save(slot);
            }

            current = slotEnd;
        }
    }
}
