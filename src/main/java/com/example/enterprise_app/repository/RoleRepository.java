package com.example.enterprise_app.repository;

import com.example.enterprise_app.model.ERole;
import com.example.enterprise_app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}