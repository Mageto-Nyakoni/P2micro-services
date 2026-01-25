package com.revature.smartAppointment.dto;

import lombok.Data;


@Data
public class SlotDto {

    private Integer slotId;
    private String time;
    private boolean available;

  // No-arg constructor
    public SlotDto() {
    }

    // All-args constructor
    public SlotDto(Integer slotId, String time, boolean available) {
        this.slotId = slotId;
        this.time = time;
        this.available = available;
    }

    // Getters and setters
    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
