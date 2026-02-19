package com.revature.AppointmentService.dto.response;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityDto {
    private Integer doctorId;
    private String doctorName;
    private String specialization;
    private List<SlotDto> slots;
}
