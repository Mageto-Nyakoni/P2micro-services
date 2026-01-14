package com.revature.smartAppointment.Model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "speciality")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Speciality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "speciality_id")
    private Integer specialityId;

    @Column(name = "speciality_name")
    private String specialityName;

    @Column(name="description")
    private String description;
}