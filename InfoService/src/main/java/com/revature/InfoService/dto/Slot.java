package com.revature.InfoService.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Slot {
    private Integer slotId;
    private String startTime;
    private String endTime;
    private boolean available;
}
