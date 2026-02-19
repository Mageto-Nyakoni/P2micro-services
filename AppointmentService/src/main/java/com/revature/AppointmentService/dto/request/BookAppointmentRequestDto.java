package com.revature.AppointmentService.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentRequestDto {
    private Integer patientId;
    private Integer doctorId;
    private Integer slotId;
    private Integer appointmentTypeId;
}

