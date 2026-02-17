package com.revature.Service;

import java.util.*;

import org.springframework.stereotype.Service;

import com.revature.Model.TimeSlot;
import com.revature.Model.enums.TimeSlotStatus;
import com.revature.Repository.TimeSlotRepository;
import com.revature.dto.DoctorAvailabilityDto;
import com.revature.dto.SlotDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final TimeSlotRepository timeSlotRepository;
    private final DoctorInfoClient doctorInfoClient;

    public Map<String, List<DoctorAvailabilityDto>> getAvailability() {

        List<TimeSlot> availableSlots =
                timeSlotRepository.findByStatus(TimeSlotStatus.AVAILABLE);

        Map<String, List<DoctorAvailabilityDto>> result = new HashMap<>();
        Map<Integer, String> doctorNameCache = new HashMap<>();

        Map<String, Map<Integer, DoctorAvailabilityDto>> temp = new HashMap<>();

        for (TimeSlot slot : availableSlots) {
            String date = slot.getDateAvailable().toString();

            temp.putIfAbsent(date, new HashMap<>());

            var doctorMap = temp.get(date);

            Integer doctorId = slot.getDoctorId();
            String doctorName = resolveDoctorName(doctorId, doctorNameCache);

            doctorMap.computeIfAbsent(doctorId, id -> {
                DoctorAvailabilityDto dto = new DoctorAvailabilityDto();
                dto.setDoctorId(id);
                dto.setDoctorName(doctorName);
                return dto;
            });

            DoctorAvailabilityDto doctorDto = doctorMap.get(doctorId);
          
            SlotDto slotDto = new SlotDto();
            slotDto.setSlotId(slot.getSlotId());
            slotDto.setStartTime(slot.getStartTime().toString());
            slotDto.setEndTime(slot.getEndTime().toString());
            slotDto.setAvailable(slot.getStatus() == TimeSlotStatus.AVAILABLE);

            doctorDto.getSlots().add(slotDto);
        }

        // convert to final structure
        temp.forEach((date, doctors) ->
            result.put(date, new ArrayList<>(doctors.values()))
        );

        return result;
    }

    private String resolveDoctorName(Integer doctorId, Map<Integer, String> doctorNameCache) {
        if (doctorId == null) {
            return null;
        }
        if (!doctorNameCache.containsKey(doctorId)) {
            doctorNameCache.put(doctorId, doctorInfoClient.getDoctorName(doctorId));
        }
        return doctorNameCache.get(doctorId);
    }
}
