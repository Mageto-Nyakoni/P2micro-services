package com.revature.InfoService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.InfoService.model.Speciality;

@Repository
public interface SpecialityRepository extends JpaRepository <Speciality, Integer> {
    public Optional<Speciality> findSpecialityBySpecialityName(String speciality_name);
}