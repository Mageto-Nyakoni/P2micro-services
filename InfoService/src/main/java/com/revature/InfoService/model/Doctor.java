package com.revature.InfoService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "gender")
    private String gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "speciality_id")
    private Speciality speciality;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Column(name = "user_id")
    private Integer userId;

    public Doctor (Integer userId, Integer experienceYears, String gender, Speciality speciality, String bio){
        this.userId = userId;
        this.experienceYears = experienceYears;
        this.gender = gender;
        this.speciality = speciality;
        this.bio = bio;
    }
}