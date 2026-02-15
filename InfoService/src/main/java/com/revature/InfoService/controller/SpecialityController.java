package com.revature.InfoService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.InfoService.model.Speciality;
import com.revature.InfoService.service.SpecialityService;

@RestController
@RequestMapping("/smart-appointment/api/specialities")
@CrossOrigin("*")
public class SpecialityController {
    private SpecialityService specialityService;

    @Autowired
    public SpecialityController(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }

    @GetMapping()
    public ResponseEntity<List<Speciality>> getAllSpecialities() {
        List<Speciality> specialities = specialityService.findAll();
        /*
        List<SpecialityResponse> responses = new ArrayList<>();
        for (Speciality speciality : specialities) {
            responses.add(new SpecialityResponse(speciality.getSpecialityId(), speciality.getSpecialityName()));
        }
        */
        return ResponseEntity.ok(specialities);
    }
}
