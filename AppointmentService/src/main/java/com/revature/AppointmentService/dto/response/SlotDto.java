package com.revature.AppointmentService.dto.response;

import java.time.LocalDateTime;
import lombok.*;
import lombok.Data;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto {
     private Integer slotId;
    private Integer doctorId;
    private LocalDateTime dateTime;
    private String startTime;
    private String endTime;
    private boolean available;

// getter and setters
   // Getters and setters
    public Integer getSlotId() { return slotId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
