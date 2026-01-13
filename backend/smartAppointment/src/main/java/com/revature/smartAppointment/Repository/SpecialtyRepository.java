package com.revature.smartAppointment.Repository;

import com.revature.smartAppointment.Model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
@Repository
public interface SpecialtyRepository extends JpaRepository <Specialty, Integer>{
}
