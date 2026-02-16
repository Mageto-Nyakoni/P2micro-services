package com.revature.dto.response;

import lombok.Data;

@Data
public class SlotDto {
     private Integer slotId;
    private String startTime;
    private String endTime;
    private boolean available;
}
