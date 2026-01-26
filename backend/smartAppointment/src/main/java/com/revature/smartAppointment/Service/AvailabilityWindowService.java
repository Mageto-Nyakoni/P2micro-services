package com.revature.smartAppointment.Service;

import com.revature.smartAppointment.Model.*;
import com.revature.smartAppointment.Repository.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.revature.smartAppointment.dto.AvailabilityWindowDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AvailabilityWindowService {

    private final AvailabilityWindowRepository windowRepository;
    private final DoctorRepository doctorRepository;
    private final TimeSlotService timeSlotService;

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
        timeSlotService.generateSlotsForWindow(window);

        return window;
    }

    @Transactional
    public Map<String, Object> deactivateWindow(Integer windowId) {
        AvailabilityWindow window = windowRepository.findById(windowId)
                .orElseThrow(() -> new RuntimeException("Availability window not found"));
        if (window.isActive()) {
            window.setActive(false);
            windowRepository.save(window);
        }

        return timeSlotService.blockBreakPeriod(
                window.getDoctor().getDoctorId(),
                window.getDate(),
                window.getStartTime(),
                window.getEndTime()
        );
    }

  /* public List<AvailabilityWindow> getWindowsForDoctor(Integer doctorId) {
    return windowRepository.findByDoctor_DoctorIdAndActiveTrue(doctorId);
}*/

@Transactional
public List<AvailabilityWindowDTO> getWindowsForDoctor(Integer doctorId) {
    List<AvailabilityWindow> windows = windowRepository.findActiveWindowsByDoctorIdWithDoctor(doctorId);

    return windows.stream().map(w -> new AvailabilityWindowDTO(
        w.getWindowId(),
        w.getDate(),
        w.getStartTime(),
        w.getEndTime(),
        w.isActive(),
        w.getDoctor().getDoctorId(),
         w.getDoctor().getUser().getFirstName() + " " + w.getDoctor().getUser().getLastName()
    )).collect(Collectors.toList());
}


}
