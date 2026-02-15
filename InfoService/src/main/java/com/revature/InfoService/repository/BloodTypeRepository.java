package com.revature.InfoService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.InfoService.model.BloodType;

@Repository
public interface BloodTypeRepository extends JpaRepository<BloodType, Integer>{
    Optional<BloodType> findBloodTypeByName(String name);
}   
