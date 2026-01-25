package com.revature.smartAppointment.dto;

import java.util.List;

import lombok.Data;


import java.util.ArrayList;


@Data
public class DoctorAvailabilityDto {
   private Integer doctorId;
    private String doctorName;
    private String specialization;
    private List<SlotDto> slots;

 // NO-ARG constructor (required for your current service code)
    public DoctorAvailabilityDto() {
        this.slots = new ArrayList<>();
    }

     // All-args constructor
    public DoctorAvailabilityDto(Integer doctorId, String doctorName, List<SlotDto> slots) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.slots = slots;
    }
    // getters + setters
 public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public List<SlotDto> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotDto> slots) {
        this.slots = slots;
    }

}
