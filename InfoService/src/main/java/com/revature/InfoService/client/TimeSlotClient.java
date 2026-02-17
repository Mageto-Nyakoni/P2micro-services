package com.revature.InfoService.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.revature.InfoService.dto.Slot;
import com.revature.InfoService.dto.TimeSlot;

@FeignClient(name = "ScheduleService", contextId = "timeSlotService", path = "/smart-appointment/api/slots")
public interface TimeSlotClient {
    @GetMapping("/doctor/{doctorId}")
    List<Slot> getDoctorSlotsByDate(@PathVariable Integer doctorId, @RequestParam String date);

    @PatchMapping("/{slotId}/status")
    TimeSlot updateSlotStatus(@PathVariable Integer slotId, @RequestParam String status);
}
