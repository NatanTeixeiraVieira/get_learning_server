package com.example.get_learning_server.repository;

import com.example.get_learning_server.entity.Role;
import com.example.get_learning_server.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
  Role findByName(UserRoles name);
}
