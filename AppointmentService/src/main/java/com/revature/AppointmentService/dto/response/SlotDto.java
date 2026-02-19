package com.revature.AppointmentService.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto {
    private Integer slotId;
    private String startTime;
    private String endTime;
    private boolean available;
}
