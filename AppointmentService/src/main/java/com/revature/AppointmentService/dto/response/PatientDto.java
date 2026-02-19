package com.revature.AppointmentService.dto.response;

public record PatientDto(
    Integer patientId,
    String firstName,
    String lastName,
    String email
) {}
