package com.keer.spring.securityJpa;

import com.keer.spring.securityJpa.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class SecurityJpaApplication {

  public static void main(String[] args) {
    SpringApplication.run(SecurityJpaApplication.class, args);
  }
}
