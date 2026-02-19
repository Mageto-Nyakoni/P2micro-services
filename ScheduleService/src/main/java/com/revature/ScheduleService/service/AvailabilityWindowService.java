package com.revature.ScheduleService.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.revature.ScheduleService.model.AvailabilityWindow;
import com.revature.ScheduleService.model.TimeSlot;
import com.revature.ScheduleService.model.enums.TimeSlotStatus;
import com.revature.ScheduleService.repository.AvailabilityWindowRepository;
import com.revature.ScheduleService.dto.AvailabilityWindowDTO;
import com.revature.ScheduleService.dto.DoctorAvailabilityDto;
import com.revature.ScheduleService.dto.SlotDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityWindowService {

    private final AvailabilityWindowRepository windowRepository;
    private final TimeSlotService timeSlotService;
    private final DoctorInfoService doctorInfoService;

    @Transactional
    public AvailabilityWindow createWindow(
            Integer doctorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        AvailabilityWindow window = AvailabilityWindow.builder()
                .doctorId(doctorId)
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
    public AvailabilityWindowDTO createWindowDto(
            Integer doctorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        AvailabilityWindow window = createWindow(doctorId, date, startTime, endTime);
        return new AvailabilityWindowDTO(
                window.getWindowId(),
                window.getDate(),
                window.getStartTime(),
                window.getEndTime(),
                window.isActive(),
                window.getDoctorId(),
                doctorInfoService.getDoctorName(window.getDoctorId())
        );
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
                window.getDoctorId(),
                window.getDate(),
                window.getStartTime(),
                window.getEndTime()
        );
    }

    @Transactional
    public List<AvailabilityWindowDTO> getWindowsForDoctor(Integer doctorId) {
        List<AvailabilityWindow> windows = windowRepository.findActiveWindowsByDoctorIdWithDoctor(doctorId);
        String doctorName = doctorInfoService.getDoctorName(doctorId);

        return windows.stream().map(w -> new AvailabilityWindowDTO(
                w.getWindowId(),
                w.getDate(),
                w.getStartTime(),
                w.getEndTime(),
                w.isActive(),
                w.getDoctorId(),
                doctorName
        )).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, List<DoctorAvailabilityDto>> getAvailabilityByDate() {
        List<TimeSlot> slots = timeSlotService.getAvailableSlots();

        Map<String, List<DoctorAvailabilityDto>> result = new HashMap<>();
        Map<Integer, String> doctorNameCache = new HashMap<>();

        for (TimeSlot slot : slots) {
            String dateKey = slot.getDateAvailable().toString();
            Integer doctorId = slot.getDoctorId();

            DoctorAvailabilityDto doctorDTO = new DoctorAvailabilityDto();
            doctorDTO.setDoctorId(doctorId);
            doctorDTO.setDoctorName(resolveDoctorName(doctorId, doctorNameCache));

            SlotDto slotDTO = new SlotDto();
            slotDTO.setSlotId(slot.getSlotId());
            slotDTO.setStartTime(slot.getStartTime().toString());
            slotDTO.setEndTime(slot.getEndTime().toString());
            slotDTO.setAvailable(slot.getStatus() == TimeSlotStatus.AVAILABLE);

            result.computeIfAbsent(dateKey, k -> new ArrayList<>());
            List<DoctorAvailabilityDto> doctorsForDate = result.get(dateKey);

            Optional<DoctorAvailabilityDto> existingDoctor = doctorsForDate.stream()
                    .filter(d -> d.getDoctorId().equals(doctorId))
                    .findFirst();

            if (existingDoctor.isPresent()) {
                existingDoctor.get().getSlots().add(slotDTO);
            } else {
                doctorDTO.getSlots().add(slotDTO);
                doctorsForDate.add(doctorDTO);
            }
        }

        return result;
    }

    private String resolveDoctorName(Integer doctorId, Map<Integer, String> doctorNameCache) {
        if (doctorId == null) {
            return null;
        }
        if (!doctorNameCache.containsKey(doctorId)) {
            doctorNameCache.put(doctorId, doctorInfoService.getDoctorName(doctorId));
        }
        return doctorNameCache.get(doctorId);
    }
}
