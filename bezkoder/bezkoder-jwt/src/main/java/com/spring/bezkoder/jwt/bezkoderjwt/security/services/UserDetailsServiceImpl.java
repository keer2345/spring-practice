package com.spring.bezkoder.jwt.bezkoderjwt.security.services;

import com.spring.bezkoder.jwt.bezkoderjwt.entity.User;
import com.spring.bezkoder.jwt.bezkoderjwt.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired UserRepo userRepo;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepo
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with username " + username));
    return UserDetailsImpl.build(user);
  }
}
