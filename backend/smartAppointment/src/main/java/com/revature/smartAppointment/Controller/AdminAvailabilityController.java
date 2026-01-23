package com.revature.smartAppointment.Controller;

import com.revature.smartAppointment.Service.AvailabilityWindowService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/doctors")
public class AdminAvailabilityController {

    private final AvailabilityWindowService availabilityWindowService;

    public AdminAvailabilityController(AvailabilityWindowService availabilityWindowService) {
        this.availabilityWindowService = availabilityWindowService;
    }

    @PostMapping("/{doctorId}/availability-windows")
    public void createAvailabilityWindow(
            @PathVariable Integer doctorId,
            @RequestBody AvailabilityWindowRequest request
    ) {
        availabilityWindowService.createWindow(
                doctorId,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    public static class AvailabilityWindowRequest {

        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }
    }
}
