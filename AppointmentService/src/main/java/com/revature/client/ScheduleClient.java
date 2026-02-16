package com.revature.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.revature.dto.response.SlotDto;
@FeignClient(name = "schedule-service")
public interface ScheduleClient {

    @GetMapping("/timeslots/{id}")
    SlotDto getTimeSlot(@PathVariable("id") Long id);
}
