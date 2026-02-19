package com.revature.AppointmentService.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentRequestDto {
    
    private Integer patientId;
    private Integer doctorId;
    private Integer slotId;
    private Integer appointmentTypeId;

     public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public Integer getAppointmentTypeId() {
        return appointmentTypeId;
    }

    public void setAppointmentTypeId(Integer appointmentTypeId) {
        this.appointmentTypeId = appointmentTypeId;
    }
}

