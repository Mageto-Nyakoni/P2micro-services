package com.revature.smartAppointment.Controller;
import java.util.List;
import com.revature.smartAppointment.dto.AvailabilityWindowDTO;
import com.revature.smartAppointment.Model.AvailabilityWindow;
import com.revature.smartAppointment.Service.AvailabilityWindowService;
import org.springframework.web.bind.annotation.*;
import com.revature.smartAppointment.Model.AvailabilityWindow;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAvailabilityController {

    private final AvailabilityWindowService availabilityWindowService;

    public AdminAvailabilityController(AvailabilityWindowService availabilityWindowService) {
        this.availabilityWindowService = availabilityWindowService;
    }
    
   /*  @GetMapping("/{doctorId}/availability-windows")
public List<AvailabilityWindow> getAvailabilityWindows(
        @PathVariable Integer doctorId
) {
    return availabilityWindowService.getWindowsForDoctor(doctorId);
}*/
    
    @GetMapping("/{doctorId}/availability-windows")
public List<AvailabilityWindowDTO> getAvailabilityWindows(
        @PathVariable Integer doctorId
) {
    return availabilityWindowService.getWindowsForDoctor(doctorId);
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
