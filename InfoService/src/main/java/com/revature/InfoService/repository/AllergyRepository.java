package com.revature.InfoService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.InfoService.model.Allergy;

@Repository
public interface AllergyRepository extends JpaRepository<Allergy, Integer> {
    Optional<Allergy> findAllergyByName(String name);
}
