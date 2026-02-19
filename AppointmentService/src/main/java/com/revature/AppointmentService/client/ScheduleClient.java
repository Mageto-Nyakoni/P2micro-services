package com.revature.AppointmentService.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.revature.AppointmentService.dto.response.SlotDto;

// is the slot exist - scheduleservice
@FeignClient(name = "ScheduleService")
public interface ScheduleClient {

    @GetMapping("/slots/{slotId}")
    SlotDto getTimeSlot(@PathVariable Integer slotId);

    
//===================ScheduleService should mark slot as booked=======================
     @PutMapping("/slots/{slotId}/book")
    void bookSlot(@PathVariable Integer slotId);
}
