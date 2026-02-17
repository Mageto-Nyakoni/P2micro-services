package com.revature.AppointmentService.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.revature.AppointmentService.dto.response.SlotDto;

// is the slot exist - scheduleservice
@FeignClient(name = "schedule-service")
public interface ScheduleClient {

    @GetMapping("/timeslots/{id}")
    SlotDto getTimeSlot(@PathVariable("id") Long id);
}
