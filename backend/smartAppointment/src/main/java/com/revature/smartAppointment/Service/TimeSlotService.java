package com.revature.smartAppointment.Service;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TimeSlotService {

    private static final Logger log = LoggerFactory.getLogger(TimeSlotService.class);

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
    public List<PublicTimeSlotView> getAvailableSlotsPublic(Integer doctorId, LocalDate date, LocalDate from, LocalDate to) {
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
            if (start != null && end != null && start.isAfter(end)) {
                LocalDate tmp = start;
                start = end;
                end = tmp;
            }
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

        if (log.isDebugEnabled()) {
            long total = timeSlotRepository.count();
            long available = timeSlotRepository.countByStatus(TimeSlotStatus.AVAILABLE);
            Long filteredAvailable = null;
            if (date != null) {
                if (doctorId != null) {
                    filteredAvailable = timeSlotRepository.countByDoctor_DoctorIdAndDateAvailableAndStatus(
                            doctorId, date, TimeSlotStatus.AVAILABLE
                    );
                } else {
                    filteredAvailable = timeSlotRepository.countByDateAvailableAndStatus(date, TimeSlotStatus.AVAILABLE);
                }
            } else if (from != null || to != null) {
                LocalDate start = from != null ? from : to;
                LocalDate end = to != null ? to : from;
                if (start != null && end != null && start.isAfter(end)) {
                    LocalDate tmp = start;
                    start = end;
                    end = tmp;
                }
                if (doctorId != null) {
                    filteredAvailable = timeSlotRepository.countByDoctor_DoctorIdAndDateAvailableBetweenAndStatus(
                            doctorId, start, end, TimeSlotStatus.AVAILABLE
                    );
                } else {
                    filteredAvailable = timeSlotRepository.countByDateAvailableBetweenAndStatus(
                            start, end, TimeSlotStatus.AVAILABLE
                    );
                }
            } else if (doctorId != null) {
                filteredAvailable = timeSlotRepository.countByDoctor_DoctorIdAndStatus(
                        doctorId, TimeSlotStatus.AVAILABLE
                );
            }
            log.debug(
                    "Public time-slots query counts: total={}, available={}, filteredAvailable={}, doctorId={}, date={}, from={}, to={}",
                    total, available, filteredAvailable, doctorId, date, from, to
            );
        }

        List<PublicTimeSlotView> results = new ArrayList<>();
        for (TimeSlot slot : slots) {
            Integer doctorIdValue = slot.getDoctor() != null ? slot.getDoctor().getDoctorId() : null;
            String doctorName = null;
            if (slot.getDoctor() != null && slot.getDoctor().getUser() != null) {
                String name = slot.getDoctor().getUser().getFirstName() + " " + slot.getDoctor().getUser().getLastName();
                doctorName = name;
            }
            results.add(new PublicTimeSlotView(
                    slot.getSlotId(),
                    doctorIdValue,
                    doctorName,
                    slot.getDateAvailable(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    slot.getStatus()
            ));
        }

        return results;
    }

    public static class PublicTimeSlotView {
        private Integer slotId;
        private Integer doctorId;
        private String doctorName;
        private LocalDate dateAvailable;
        private LocalTime startTime;
        private LocalTime endTime;
        private TimeSlotStatus status;

        public PublicTimeSlotView(
                Integer slotId,
                Integer doctorId,
                String doctorName,
                LocalDate dateAvailable,
                LocalTime startTime,
                LocalTime endTime,
                TimeSlotStatus status
        ) {
            this.slotId = slotId;
            this.doctorId = doctorId;
            this.doctorName = doctorName;
            this.dateAvailable = dateAvailable;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
        }

        public Integer getSlotId() { return slotId; }
        public Integer getDoctorId() { return doctorId; }
        public String getDoctorName() { return doctorName; }
        public LocalDate getDateAvailable() { return dateAvailable; }
        public LocalTime getStartTime() { return startTime; }
        public LocalTime getEndTime() { return endTime; }
        public TimeSlotStatus getStatus() { return status; }
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
}
