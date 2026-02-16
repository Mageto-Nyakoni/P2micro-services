package com.revature.InfoService.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentPatientView {
    private Integer appointmentId;
    private String doctorName;
    private String appointmentType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public AppointmentPatientView(String doctorName, String appointmentType, LocalDateTime startTime, LocalDateTime endTime, String status){
        this.doctorName = doctorName;
        this.appointmentType = appointmentType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
