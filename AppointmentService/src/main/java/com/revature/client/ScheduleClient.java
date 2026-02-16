package com.revature.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "schedule-service")
public interface ScheduleClient {

    @GetMapping("/timeslots/{id}")
    Object getTimeSlot(@PathVariable("id") Long id);
}
