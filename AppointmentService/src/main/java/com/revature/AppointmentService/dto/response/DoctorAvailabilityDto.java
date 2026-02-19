package com.revature.AppointmentService.dto.response;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityDto {
    private Integer doctorId;
    private String doctorName;
    private String specialization;
    private List<SlotDto> slots;
    // Getters and setters
    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public List<SlotDto> getSlots() { return slots; }
    public void setSlots(List<SlotDto> slots) { this.slots = slots; }

}
