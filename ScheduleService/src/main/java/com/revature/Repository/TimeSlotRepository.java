package com.revature.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.revature.Model.TimeSlot;
import com.revature.Model.enums.TimeSlotStatus;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    List<TimeSlot> findByDoctorId(Integer doctorId);

    boolean existsByDoctorIdAndDateAvailableAndStartTime(
            Integer doctorId,
            LocalDate dateAvailable,
            LocalTime startTime
    );
    

    List<TimeSlot> findByDoctorIdAndDateAvailableOrderByStartTimeAsc(Integer doctorId, LocalDate dateAvailable);

    List<TimeSlot> findByDoctorIdAndDateAvailableAndStatusOrderByStartTimeAsc(Integer doctorId, LocalDate dateAvailable, TimeSlotStatus status);

    List<TimeSlot> findByDateAvailableAndStatusOrderByStartTimeAsc(LocalDate dateAvailable, TimeSlotStatus status);

    List<TimeSlot> findByDoctorIdAndDateAvailableBetweenOrderByDateAvailableAscStartTimeAsc(Integer doctorId, LocalDate start, LocalDate end);

    List<TimeSlot> findByDoctorIdAndDateAvailableBetweenAndStatusOrderByDateAvailableAscStartTimeAsc(Integer doctorId, LocalDate start, LocalDate end, TimeSlotStatus status);

    List<TimeSlot> findByDoctorIdAndDateAvailableBetween(Integer doctorId, LocalDate start, LocalDate end);

    List<TimeSlot> findByDoctorIdOrderByDateAvailableAscStartTimeAsc(Integer doctorId);

    List<TimeSlot> findByDoctorIdAndStatusOrderByDateAvailableAscStartTimeAsc(Integer doctorId, TimeSlotStatus status);

    List<TimeSlot> findByDateAvailableBetweenAndStatusOrderByDateAvailableAscStartTimeAsc(LocalDate start, LocalDate end, TimeSlotStatus status);

    List<TimeSlot> findByStatusOrderByDateAvailableAscStartTimeAsc(TimeSlotStatus status);

    List<TimeSlot> findByDateAvailableBetweenOrderByDateAvailableAscStartTimeAsc(LocalDate start, LocalDate end);

    List<TimeSlot> findAllByOrderByDateAvailableAscStartTimeAsc();

    long countByStatus(TimeSlotStatus status);

    long countByDoctorIdAndStatus(Integer doctorId, TimeSlotStatus status);

    long countByDoctorIdAndDateAvailableAndStatus(
            Integer doctorId,
            LocalDate dateAvailable,
            TimeSlotStatus status
    );

    long countByDoctorIdAndDateAvailableBetweenAndStatus(
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

    List<TimeSlot> findByDoctorIdAndDateAvailableAndStartTimeGreaterThanEqualAndEndTimeLessThanEqualOrderByStartTimeAsc(
            Integer doctorId,
            LocalDate dateAvailable,
            LocalTime startTime,
            LocalTime endTime
    );
     List<TimeSlot> findByStatus(TimeSlotStatus status);
    /*List<TimeSlot> findByPatientIdAndStatusOrderByStartAt(
        Integer patientId,
        TimeSlotStatus status
           );*/

    /* ================= NEW METHOD ================= */
    @Transactional
    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'AVAILABLE' WHERE t.slotId = :slotId AND t.status = 'BOOKED'")
    int freeSlotIfBooked(Integer slotId);

    @Transactional
    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'BOOKED' WHERE t.slotId = :slotId AND t.status = 'AVAILABLE'")
    int bookSlotIfAvailable(Integer slotId);

    @Transactional
    @Modifying
    @Query("UPDATE TimeSlot t SET t.status = 'BLOCKED' WHERE t.slotId IN :slotIds AND t.status <> 'BOOKED'")
    int blockSlotsIfNotBooked(@Param("slotIds") List<Integer> slotIds);
}
