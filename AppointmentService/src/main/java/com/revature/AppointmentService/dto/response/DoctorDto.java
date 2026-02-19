package com.revature.AppointmentService.dto.response;

public class DoctorDto {
    private Integer doctorId;
    private String firstName;
    private String lastName;
    private String speciality;
    public DoctorDto() {}

    public DoctorDto(Integer doctorId, String firstName, String lastName, String speciality) {
        this.doctorId = doctorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.speciality = speciality;
    }
    public Integer getDoctorId() { return doctorId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSpeciality() { return speciality; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }
}
