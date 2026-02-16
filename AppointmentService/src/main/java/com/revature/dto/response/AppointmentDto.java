package com.revature.dto.response;
import java.time.LocalDateTime;  
import com.revature.model.AppointmentStatus;
public record AppointmentDto(
    Integer appointmentId,
    String doctorName,
    String appointmentType,
    LocalDateTime startTime,
    LocalDateTime endTime,
    AppointmentStatus status
) {}

