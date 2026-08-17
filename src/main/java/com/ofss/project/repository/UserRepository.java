package com.ofss.project.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ofss.project.entity.User;
import com.ofss.project.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByMobile(String mobile);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
    
    List<User> findByRoleInOrderByCreatedAtDesc(
            Collection<UserRole> roles
    );
}
