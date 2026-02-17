package com.revature.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revature.Model.AvailabilityWindow;


public interface AvailabilityWindowRepository
        extends JpaRepository<AvailabilityWindow, Integer> {

    // doctorId is now a scalar field on AvailabilityWindow, not a relation.
    @Query("SELECT aw FROM AvailabilityWindow aw " +
           "WHERE aw.doctorId = :doctorId AND aw.active = true")
    List<AvailabilityWindow> findActiveWindowsByDoctorIdWithDoctor(@Param("doctorId") Integer doctorId);

}
