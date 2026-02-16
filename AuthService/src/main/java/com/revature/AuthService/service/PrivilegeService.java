package com.revature.AuthService.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.AuthService.model.Privilege;
import com.revature.AuthService.repository.PrivilegeRepository;

@Service
public class PrivilegeService implements ServiceInterface<Privilege> {
    private final PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    @Override
    public Privilege save(Privilege entity) {
        return privilegeRepository.save(entity);
    }

    @Override
    public Optional<Privilege> findById(int id) {
        return privilegeRepository.findById(id);
    }

    @Override
    public List<Privilege> findAll() {
        return privilegeRepository.findAll();
    }

    @Override
    public Optional<Privilege> deleteById(int id) {
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(id);
        if (optionalPrivilege.isPresent()) {
            privilegeRepository.deleteById(id);
        }
        return optionalPrivilege;
    }

    @Override
    public Privilege updateById(int id, Privilege newPrivilege) {
        Optional<Privilege> optionalPrivilege = privilegeRepository.findById(id);
        if (optionalPrivilege.isPresent()) {
            Privilege privilege = optionalPrivilege.get();
            privilege.setRoleName(newPrivilege.getRoleName());
            return privilegeRepository.save(privilege);
        }
        return null;
    }
}
