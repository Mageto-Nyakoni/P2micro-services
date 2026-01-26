package com.revature.smartAppointment.Service;

<<<<<<< HEAD
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.smartAppointment.Model.AvailabilityWindow;
import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
=======
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;
import com.revature.smartAppointment.Model.enums.AppointmentStatus;
import com.revature.smartAppointment.Repository.TimeSlotRepository;
import com.revature.smartAppointment.dto.AppointmentDto;
>>>>>>> patients-appointment

@Service
public class TimeSlotService {

<<<<<<< HEAD
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public TimeSlotService(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    /**
     * Generates 30-min slots from an AvailabilityWindow.
     * Idempotent: Skips creation if a slot already exists for that doctor/time.
     */
    @Transactional
    public void generateSlotsForWindow(AvailabilityWindow window) {
        if (!window.isActive()) return;

        List<TimeSlot> slotsToSave = new ArrayList<>();

        LocalTime currentStart = window.getStartTime();

        while (!currentStart.plusMinutes(30).isAfter(window.getEndTime())) {
            LocalTime currentEnd = currentStart.plusMinutes(30);

            // Only create if it doesn't exist
            if (!timeSlotRepository.existsByDoctor_DoctorIdAndDateAvailableAndStartTime(
                    window.getDoctor().getDoctorId(),
                    window.getDate(),
                    currentStart
            )) {
                TimeSlot slot = new TimeSlot(
                        window.getDoctor(),
                        window.getDate(),
                        currentStart,
                        currentEnd
                );
                slot.setStatus(TimeSlotStatus.AVAILABLE);
                slotsToSave.add(slot);
            }

            currentStart = currentEnd;
        }

        if (!slotsToSave.isEmpty()) {
            timeSlotRepository.saveAll(slotsToSave);
        }
    }

    /**
     * Returns the raw TimeSlot entities for a specific doctor and date range.
     * Note: Be careful with LazyLoading if these entities are serialized to JSON directly 
     * without @JsonIgnore on the doctor/parent relationships.
     */
    @Transactional(readOnly = true)
    public List<TimeSlot> getSlotsForDoctorDateRange(Integer doctorId, LocalDate fromDate, LocalDate toDate) {
        return timeSlotRepository.findByDoctor_DoctorIdAndDateAvailableBetweenOrderByDateAvailableAscStartTimeAsc(
                doctorId,
                fromDate,
                toDate
        );
    }

    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableSlots(Integer doctorId, LocalDate date) {
        return timeSlotRepository.findByDoctor_DoctorIdAndDateAvailableAndStatusOrderByStartTimeAsc(
                doctorId,
                date,
                TimeSlotStatus.AVAILABLE
        );
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAvailableSlotsPublic(Integer doctorId, LocalDate date, LocalDate from, LocalDate to) {
        List<TimeSlot> slots;

        if (date != null) {
            if (doctorId != null) {
                slots = timeSlotRepository.findByDoctor_DoctorIdAndDateAvailableAndStatusOrderByStartTimeAsc(
                        doctorId, date, TimeSlotStatus.AVAILABLE
                );
            } else {
                slots = timeSlotRepository.findByDateAvailableAndStatusOrderByStartTimeAsc(
                        date, TimeSlotStatus.AVAILABLE
                );
            }
        } else if (from != null || to != null) {
            LocalDate start = from != null ? from : to;
            LocalDate end = to != null ? to : from;
            if (doctorId != null) {
                slots = timeSlotRepository.findByDoctor_DoctorIdAndDateAvailableBetweenAndStatusOrderByDateAvailableAscStartTimeAsc(
                        doctorId, start, end, TimeSlotStatus.AVAILABLE
                );
            } else {
                slots = timeSlotRepository.findByDateAvailableBetweenAndStatusOrderByDateAvailableAscStartTimeAsc(
                        start, end, TimeSlotStatus.AVAILABLE
                );
            }
        } else if (doctorId != null) {
            slots = timeSlotRepository.findByDoctor_DoctorIdAndStatusOrderByDateAvailableAscStartTimeAsc(
                    doctorId, TimeSlotStatus.AVAILABLE
            );
        } else {
            slots = timeSlotRepository.findByStatusOrderByDateAvailableAscStartTimeAsc(TimeSlotStatus.AVAILABLE);
        }

        List<Map<String, Object>> results = new ArrayList<>();
        for (TimeSlot slot : slots) {
            Map<String, Object> item = new HashMap<>();
            item.put("slotId", slot.getSlotId());
            item.put("doctorId", slot.getDoctor() != null ? slot.getDoctor().getDoctorId() : null);
            if (slot.getDoctor() != null && slot.getDoctor().getUser() != null) {
                String name = slot.getDoctor().getUser().getFirstName() + " " + slot.getDoctor().getUser().getLastName();
                item.put("doctorName", name);
            } else {
                item.put("doctorName", null);
            }
            item.put("dateAvailable", slot.getDateAvailable());
            item.put("startTime", slot.getStartTime());
            item.put("endTime", slot.getEndTime());
            item.put("status", slot.getStatus());
            results.add(item);
        }

        return results;
    }

    /**
     * Blocks available slots in the given range.
     * Skips slots that are already BOOKED.
     * * Returns a Map containing:
     * - "blockedCount" (Integer): Number of slots successfully blocked
     * - "skippedSlotIds" (List<Integer>): IDs of booked slots that couldn't be blocked
     */
    @Transactional
    public Map<String, Object> blockBreakPeriod(Integer doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<TimeSlot> slotsInRange = timeSlotRepository
                .findByDoctor_DoctorIdAndDateAvailableAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualOrderByStartTimeAsc(
                        doctorId,
                        date,
                        startTime,
                        endTime
                );

        List<Integer> slotsToBlock = new ArrayList<>();
        List<Integer> skippedSlotIds = new ArrayList<>();

        for (TimeSlot slot : slotsInRange) {
            if (slot.getStatus() == TimeSlotStatus.BOOKED) {
                skippedSlotIds.add(slot.getSlotId());
            } else {
                slotsToBlock.add(slot.getSlotId());
            }
        }

        int blockedCount = 0;
        if (!slotsToBlock.isEmpty()) {
            blockedCount = timeSlotRepository.blockSlotsIfNotBooked(slotsToBlock);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("blockedCount", blockedCount);
        result.put("skippedSlotIds", skippedSlotIds);
        result.put("message", skippedSlotIds.isEmpty() ? "Success" : "Partial success: Some slots were booked.");
        
        return result;
    }

    /**
     * Cancels an appointment (frees the slot).
     * Only works if status is BOOKED.
     */
    @Transactional
    public boolean freeSlot(Integer slotId) {
        int rowsUpdated = timeSlotRepository.freeSlotIfBooked(slotId);
        return rowsUpdated > 0;
    }

    /**
     * Books a slot.
     * Only works if status is AVAILABLE.
     */
    @Transactional
    public boolean bookSlot(Integer slotId) {
        int rowsUpdated = timeSlotRepository.bookSlotIfAvailable(slotId);
        return rowsUpdated > 0;
    }
=======
    @Autowired
    private TimeSlotRepository timeSlotRepository;
 public List<TimeSlot> getAvailableSlotsForDoctor(Integer doctorId) {
        return timeSlotRepository.findByDoctorDoctorId(doctorId).stream()
                .filter(slot -> slot.getStatus() == TimeSlotStatus.AVAILABLE)
                .toList();
    }
  
>>>>>>> patients-appointment
}
