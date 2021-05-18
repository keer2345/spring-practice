package com.spring.bezkoder.jwt.bezkoderjwt.repository;

import com.spring.bezkoder.jwt.bezkoderjwt.entity.Role;
import com.spring.bezkoder.jwt.bezkoderjwt.entity.audit.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
