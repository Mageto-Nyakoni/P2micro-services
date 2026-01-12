package com.revature.smartAppointment.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Integer doctorId;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name="experience_years", nullable = false)
    private Integer experienceYears;

    @Column (name="gender", nullable = false)
    private String gender;

    @ManyToOne (optional = false)
    @JoinColumn(name="specialty_id", nullable = false)
    private Specialty specialty;

    @Column(name = "bio", nullable = true, length = 1000)
    private String bio;
}