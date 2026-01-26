package com.revature.smartAppointment.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.revature.smartAppointment.Model.TimeSlot;
import com.revature.smartAppointment.Model.enums.TimeSlotStatus;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    boolean existsByDoctor_DoctorIdAndDateAvailableAndStartTime(Integer doctorId, LocalDate dateAvailable, LocalTime startTime);

    List<TimeSlot> findByDoctor_DoctorIdAndDateAvailableOrderByStartTimeAsc(Integer doctorId, LocalDate dateAvailable);

    List<TimeSlot> findByDoctor_DoctorIdAndDateAvailableAndStatusOrderByStartTimeAsc(Integer doctorId, LocalDate dateAvailable, TimeSlotStatus status);

    List<TimeSlot> findByDateAvailableAndStatusOrderByStartTimeAsc(LocalDate dateAvailable, TimeSlotStatus status);

    List<TimeSlot> findByDoctor_DoctorIdAndDateAvailableBetweenOrderByDateAvailableAscStartTimeAsc(Integer doctorId, LocalDate start, LocalDate end);

    List<TimeSlot> findByDoctor_DoctorIdAndDateAvailableBetweenAndStatusOrderByDateAvailableAscStartTimeAsc(Integer doctorId, LocalDate start, LocalDate end, TimeSlotStatus status);

    List<TimeSlot> findByDoctor_DoctorIdAndDateAvailableBetween(Integer doctorId, LocalDate start, LocalDate end);

    List<TimeSlot> findByDoctor_DoctorIdOrderByDateAvailableAscStartTimeAsc(Integer doctorId);

    List<TimeSlot> findByDoctor_DoctorIdAndStatusOrderByDateAvailableAscStartTimeAsc(Integer doctorId, TimeSlotStatus status);

    List<TimeSlot> findByDateAvailableBetweenAndStatusOrderByDateAvailableAscStartTimeAsc(LocalDate start, LocalDate end, TimeSlotStatus status);

    List<TimeSlot> findByStatusOrderByDateAvailableAscStartTimeAsc(TimeSlotStatus status);

    long countByStatus(TimeSlotStatus status);

    long countByDoctor_DoctorIdAndStatus(Integer doctorId, TimeSlotStatus status);

    long countByDoctor_DoctorIdAndDateAvailableAndStatus(
            Integer doctorId,
            LocalDate dateAvailable,
            TimeSlotStatus status
    );

    long countByDoctor_DoctorIdAndDateAvailableBetweenAndStatus(
            Integer doctorId,
            LocalDate start,
            LocalDate end,
            TimeSlotStatus status
    );

    long countByDateAvailableAndStatus(LocalDate dateAvailable, TimeSlotStatus status);

    long countByDateAvailableBetweenAndStatus(
            LocalDate start,
            LocalDate end,
            TimeSlotStatus status
    );

    List<TimeSlot> findByDoctor_DoctorIdAndDateAvailableAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualOrderByStartTimeAsc(
            Integer doctorId,
            LocalDate dateAvailable,
            LocalTime startTime,
            LocalTime endTime
    );

    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'BOOKED' WHERE t.slotId = :slotId AND t.status = 'AVAILABLE'")
    @Transactional
    int bookSlotIfAvailable(@Param("slotId") Integer slotId);

    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'AVAILABLE' WHERE t.slotId = :slotId AND t.status = 'BOOKED'")
    @Transactional
    int freeSlotIfBooked(@Param("slotId") Integer slotId);

    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'HELD' WHERE t.slotId = :slotId AND t.status = 'AVAILABLE'")
    @Transactional
    int holdSlotIfAvailable(@Param("slotId") Integer slotId);

    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'AVAILABLE' WHERE t.slotId = :slotId AND t.status = 'HELD'")
    @Transactional
    int releaseHold(@Param("slotId") Integer slotId);

    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'BLOCKED' WHERE t.slotId IN :slotIds AND t.status <> 'BOOKED'")
    @Transactional
    int blockSlotsIfNotBooked(@Param("slotIds") List<Integer> slotIds);
}
