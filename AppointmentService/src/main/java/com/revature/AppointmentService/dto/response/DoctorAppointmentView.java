package com.revature.AppointmentService.dto.response;
import java.time.LocalDateTime;
import com.revature.AppointmentService.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
public class DoctorAppointmentView {
    private String patientFirstName;
    private String patientLastName;
    private Integer patientAge;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus appointmentStatus;

    public DoctorAppointmentView(String patientFirstName, String patientLastName, Integer patientAge, LocalDateTime appointmentDateTime, AppointmentStatus appointmentStatus) {
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.patientAge = patientAge;
        this.appointmentDateTime = appointmentDateTime;
        this.appointmentStatus = appointmentStatus;
    }

    // Getters and setters
    
}
