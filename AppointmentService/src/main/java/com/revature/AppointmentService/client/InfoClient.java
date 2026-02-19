package com.revature.AppointmentService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import com.revature.AppointmentService.dto.response.DoctorDto;
import com.revature.AppointmentService.dto.response.PatientDto;
// Does the patient exist & Does the doctor exist - infoservice
@FeignClient(name = "InfoService", path = "/smart-appointment/api")
public interface InfoClient {

    @GetMapping("/doctors/{id}")
    DoctorDto getDoctor(@PathVariable("id") Integer id);

    @GetMapping("/patients/{id}")
    PatientDto getPatient(@PathVariable("id") Integer id);

    @GetMapping("/doctors")
     List<DoctorDto> getAllDoctors();

}
