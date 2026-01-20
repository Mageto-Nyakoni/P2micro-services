package com.revature.smartAppointment.Repository;

<<<<<<< HEAD
=======
import java.util.List;

>>>>>>> backend-admin
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.smartAppointment.Model.TimeSlot;

<<<<<<< HEAD
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

    // Used by DoctorService to fetch a doctor's slots
=======
@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

>>>>>>> backend-admin
    List<TimeSlot> findByDoctorDoctorId(Integer doctorId);
}
