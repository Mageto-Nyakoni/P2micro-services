package com.revature.AuthService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.AuthService.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {
}
