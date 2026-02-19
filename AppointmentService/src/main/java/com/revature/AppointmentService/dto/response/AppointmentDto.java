package com.revature.AppointmentService.dto.response;
import java.time.LocalDateTime;

import com.revature.AppointmentService.model.AppointmentStatus;
import com.revature.AppointmentService.model.AppointmentType;

public record AppointmentDto(
    Integer appointmentId,
    Integer doctorId,
    Integer patientId,
    Integer slotId,
    AppointmentType appointmentType,
    LocalDateTime createdAt,
    LocalDateTime dateTimeScheduled,
    AppointmentStatus status
) {}

