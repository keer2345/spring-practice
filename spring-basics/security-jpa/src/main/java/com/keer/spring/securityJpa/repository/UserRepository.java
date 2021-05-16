package com.keer.spring.securityJpa.repository;

import com.keer.spring.securityJpa.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
Optional<User> findByUsername(String username);
}
