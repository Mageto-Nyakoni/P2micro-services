package com.revature.smartAppointment.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.smartAppointment.Model.Speciality;

@Repository
public interface SpecialityRepository extends JpaRepository <Speciality, Integer> {

}