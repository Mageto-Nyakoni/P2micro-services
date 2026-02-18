package com.revature.InfoService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revature.InfoService.model.BloodType;
import com.revature.InfoService.service.BloodTypeService;

@RestController
@RequestMapping("/smart-appointment/api/blood-types")
@CrossOrigin("*")
public class BloodTypeController {
    private BloodTypeService bloodTypeService;

    @Autowired
    public BloodTypeController(BloodTypeService bloodTypeService){
        this.bloodTypeService = bloodTypeService;
    }

    @GetMapping()
    public ResponseEntity<List<BloodType>> getBloodType(){
        return ResponseEntity.ok(bloodTypeService.findAll());
    }

    @GetMapping("/{blood_type_id}")
    public ResponseEntity<BloodType> getBloodType(@RequestHeader("Authorization") String authHeader, @PathVariable int blood_type_id){
        try {
            /*
            String token = authHeader.substring(7); 
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }
            */

            return bloodTypeService.findById(blood_type_id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());

        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
