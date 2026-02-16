package com.revature.AuthService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.revature.AuthService.model.User;
import com.revature.AuthService.response.UserTableResponse;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByEmail(String email);

    List<User> findUsersByPrivilege_PrivilegeId(int privilegeId);

    @Query("""
        SELECT new com.revature.AuthService.response.UserTableResponse(
            u.userId,
            u.firstName,
            u.lastName,
            u.email,
            p.roleName
        )
        FROM User u
        JOIN u.privilege p
        WHERE p.roleName IN ('Doctor', 'Admin', 'Super')
    """)
    List<UserTableResponse> findUsersForTable();
}
