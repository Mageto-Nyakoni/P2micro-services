package com.revature.smartAppointment.Repository;

import com.revature.smartAppointment.Model.AvailabilityWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvailabilityWindowRepository
        extends JpaRepository<AvailabilityWindow, Integer> {

                List<AvailabilityWindow> findByDoctor_DoctorIdAndActiveTrue(Integer doctorId);

}

