package com.revature.smartAppointment.Service;

import com.revature.smartAppointment.Model.*;
import com.revature.smartAppointment.Repository.*;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;

import java.util.stream.Collectors;
import com.revature.smartAppointment.dto.AvailabilityWindowDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.revature.smartAppointment.dto.DoctorAvailabilityDto;
import com.revature.smartAppointment.dto.SlotDto;
import java.util.*;

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

@Transactional
public Map<String, List<DoctorAvailabilityDto>> getAvailabilityByDate() {

    // Fetch all available slots
    List<TimeSlot> slots = timeSlotService.getAvailableSlots();

    Map<String, List<DoctorAvailabilityDto>> result = new HashMap<>();

    for (TimeSlot slot : slots) {

        String dateKey = slot.getDateAvailable().toString(); // YYYY-MM-DD
        Doctor doctor = slot.getDoctor();

        // Create DTO for the doctor
        DoctorAvailabilityDto doctorDTO = new DoctorAvailabilityDto();
        doctorDTO.setDoctorId(doctor.getDoctorId());
        doctorDTO.setDoctorName(doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName());
        // no speciality name

        // Slot DTO
        SlotDto slotDTO = new SlotDto();
        slotDTO.setSlotId(slot.getSlotId());
        slotDTO.setStartTime(slot.getStartTime().toString());
        slotDTO.setEndTime(slot.getEndTime().toString());
        slotDTO.setAvailable(slot.getStatus() == TimeSlotStatus.AVAILABLE);

        doctorDTO.getSlots().add(slotDTO);

        // Add doctor to the date entry
        result.computeIfAbsent(dateKey, k -> new ArrayList<>());

        List<DoctorAvailabilityDto> doctorsForDate = result.get(dateKey);

        // Merge slots if doctor already exists
        Optional<DoctorAvailabilityDto> existingDoctor = doctorsForDate.stream()
            .filter(d -> d.getDoctorId().equals(doctor.getDoctorId()))
            .findFirst();

        if (existingDoctor.isPresent()) {
            existingDoctor.get().getSlots().add(slotDTO);
        } else {
            doctorsForDate.add(doctorDTO);
        }
    }

    return result;
}


}
