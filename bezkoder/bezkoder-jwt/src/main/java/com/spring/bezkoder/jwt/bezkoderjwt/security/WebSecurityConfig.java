package com.spring.bezkoder.jwt.bezkoderjwt.security;

import com.spring.bezkoder.jwt.bezkoderjwt.security.jwt.AuthEntryPointJwt;
import com.spring.bezkoder.jwt.bezkoderjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired private UserDetailsServiceImpl userDetailsService;
  @Autowired private AuthEntryPointJwt unauthorizedHandler;



  @Override
  protected void configure(HttpSecurity http) throws Exception {
    super.configure(http);
  }
}
