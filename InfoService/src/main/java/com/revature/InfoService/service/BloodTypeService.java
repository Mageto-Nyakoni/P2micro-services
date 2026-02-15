package com.revature.InfoService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.revature.InfoService.model.BloodType;
import com.revature.InfoService.repository.BloodTypeRepository;

@Service
public class BloodTypeService implements ServiceInterface<BloodType>{
    private final BloodTypeRepository bloodTypeRepository;
    
    @Autowired
    public BloodTypeService(BloodTypeRepository bloodTypeRepository){
        this.bloodTypeRepository = bloodTypeRepository;
    }

    @Override
    public BloodType save(BloodType entity) {
        return bloodTypeRepository.save(entity);
    }

    @Override
    public Optional<BloodType> findById(int id) {
        return bloodTypeRepository.findById(id);
    }

    @Override
    public List<BloodType> findAll() {
        return bloodTypeRepository.findAll();
    }

    @Override
    public Optional<BloodType> deleteById(int id) {
        return Optional.empty();
    }

    @Override
    public BloodType updateById(int id, BloodType entity) {
        return null;
    }

    public Optional<BloodType> findBloodTypeByName(String name) {
        return bloodTypeRepository.findBloodTypeByName(name);
    }
}
