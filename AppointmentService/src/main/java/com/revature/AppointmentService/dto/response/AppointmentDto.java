package com.revature.AppointmentService.dto.response;
import java.time.LocalDateTime;

import com.revature.AppointmentService.model.AppointmentStatus;
public record AppointmentDto(
    Integer appointmentId,
    String doctorName,
    String appointmentType,
    LocalDateTime startTime,
    LocalDateTime endTime,
    AppointmentStatus status
) {}

