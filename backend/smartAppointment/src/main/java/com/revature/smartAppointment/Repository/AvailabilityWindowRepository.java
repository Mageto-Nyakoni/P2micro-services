package com.revature.smartAppointment.Repository;

import com.revature.smartAppointment.Model.AvailabilityWindow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityWindowRepository
        extends JpaRepository<AvailabilityWindow, Integer> {
}
