package com.revature.Controller;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.revature.Model.AvailabilityWindow;
import com.revature.Service.AvailabilityWindowService;
import com.revature.Service.AuthValidationClient;
import com.revature.Service.DoctorInfoClient;
import com.revature.dto.AvailabilityWindowDTO;

@RestController
@RequestMapping("/smart-appointment/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAvailabilityController {

    private final AvailabilityWindowService availabilityWindowService;
    private final AuthValidationClient authValidationClient;
    private final DoctorInfoClient doctorInfoClient;

    public AdminAvailabilityController(
            AvailabilityWindowService availabilityWindowService,
            AuthValidationClient authValidationClient,
            DoctorInfoClient doctorInfoClient
    ) {
        this.availabilityWindowService = availabilityWindowService;
        this.authValidationClient = authValidationClient;
        this.doctorInfoClient = doctorInfoClient;
    }
    
   /*  @GetMapping("/{doctorId}/availability-windows")
public List<AvailabilityWindow> getAvailabilityWindows(
        @PathVariable Integer doctorId
) {
    return availabilityWindowService.getWindowsForDoctor(doctorId);
}*/
    
    @GetMapping("/doctors/{doctorId}/availability-windows")
    public ResponseEntity<List<AvailabilityWindowDTO>> getAvailabilityWindows(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer doctorId
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(availabilityWindowService.getWindowsForDoctor(doctorId));
    }

    @PostMapping("/doctors/{doctorId}/availability-windows")
    public ResponseEntity<AvailabilityWindowDTO> createAvailabilityWindow(
            @PathVariable Integer doctorId,
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AvailabilityWindowRequest request
    ) {
        validateAdmin(authHeader);
        AvailabilityWindow window = availabilityWindowService.createWindow(
                doctorId,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );
        AvailabilityWindowDTO dto = new AvailabilityWindowDTO(
                window.getWindowId(),
                window.getDate(),
                window.getStartTime(),
                window.getEndTime(),
                window.isActive(),
                window.getDoctorId(),
                doctorInfoClient.getDoctorName(window.getDoctorId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/availability-windows/{windowId}")
    public ResponseEntity<Map<String, Object>> deleteAvailabilityWindow(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer windowId
    ) {
        validateAdmin(authHeader);
        return ResponseEntity.ok(availabilityWindowService.deactivateWindow(windowId));
    }

    private void validateAdmin(String authHeader) {
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        AuthValidationClient.AuthValidationResult validation = authValidationClient.validate(authHeader);
        if (!validation.valid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        String privilege = validation.privilege();
        if (!"Admin".equals(privilege) && !"Super".equals(privilege)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
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
