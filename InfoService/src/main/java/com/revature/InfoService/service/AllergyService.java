package com.revature.InfoService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.InfoService.model.Allergy;
import com.revature.InfoService.repository.AllergyRepository;

@Service
public class AllergyService implements ServiceInterface<Allergy> {
    private final AllergyRepository allergyRepository; 

    @Autowired
    public AllergyService(AllergyRepository allergyRepository){
        this.allergyRepository = allergyRepository;
    }
    
    @Override
    public Allergy save(Allergy entity) {
        return allergyRepository.save(entity);
    }

    @Override
    public Optional<Allergy> findById(int id) {
        return allergyRepository.findById(id);
    }

    @Override
    public List<Allergy> findAll() {
        return allergyRepository.findAll();
    }

    @Override
    public Optional<Allergy> deleteById(int id) {
        return Optional.empty();
    }

    @Override
    public Allergy updateById(int id, Allergy entity) {
        return null;
    }

    public Optional<Allergy> findAllergyByName(String name) {
        return allergyRepository.findAllergyByName(name);
    }
}
