package com.revature.dto.response;
import java.util.List;

import lombok.Data;
@Data
public class DoctorAvailabilityDto {
     private Integer doctorId;
    private String doctorName;
    private String specialization;
    private List<SlotDto> slots;
}
