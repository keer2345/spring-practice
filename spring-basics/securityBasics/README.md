# [Spring Security Basics](https://www.youtube.com/playlist?list=PLqq-6Pq4lTTYTEooakHchTGglSvkZAjnE)

> [Source Code](https://github.com/koushikkothagal/spring-boot-security)

## Create a Spring Boot Project with Security

``` xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## How to configure Spring Security Authentication

1. Get hold of AuthenticationManagerBuilder
2. Set the configuration on it

``` java
package com.keer.spring.securityBasics;

// ...

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    // Set your configuratioin on the auth object
    auth.inMemoryAuthentication()
        .withUser("keer")
        .password("keer")
        .roles("USER")
        .and()
        .withUser("foo")
        .password("foo")
        .roles("ADMIN");
  }

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
```

> **Notice:** Always deal with hashed password!

``` java
@Bean
public PasswordEncoder getPasswordEncoder() {
  return NoOpPasswordEncoder.getInstance();
}
```

Run:

``` sh
mvn spring-boot:run
```

## How to configure Spring Security Authorization

``` java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  // ...

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/admin")
        .hasRole("ADMIN")
        .antMatchers("/user")
        .hasAnyRole("USER", "ADMIN")
        .antMatchers("/")
        .permitAll()
        .and()
        .formLogin();
  }
}
```

Run:

``` sh
mvn spring-boot:run
```

## How to setup JDBC authentication with Spring Security from scratch

`pom.xml`:

``` xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```

``` java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired DataSource dataSource;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication()
        .dataSource(dataSource)
        .withDefaultSchema()
        .withUser(User.withUsername("user").password("pass").roles("USER"))
        .withUser(User.withUsername("admin").password("pass").roles("ADMIN"));
  }

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/admin")
        .hasRole("ADMIN")
        .antMatchers("/user")
        .hasAnyRole("USER", "ADMIN")
        .antMatchers("/")
        .permitAll()
        .and()
        .formLogin();
  }
}
```

Run and try it.

**JDBC to database**

See details
at [User Schema](https://docs.spring.io/spring-security/site/docs/current/reference/html5/#user-schema)
.

`resources/schema.sql`:

``` sql
create table users(
    username varchar_ignorecase(50) not null primary key,
    password varchar_ignorecase(50) not null,
    enabled boolean not null
);

create table authorities (
    username varchar_ignorecase(50) not null,
    authority varchar_ignorecase(50) not null,
    constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);
```

``` sql
INSERT INTO users (username,password,enabled) VALUES ('user','pass',true);
INSERT INTO users (username,password,enabled) VALUES ('admin','pass',true);

INSERT INTO authorities(username,authority) VALUES ('user', 'ROLE_USER');
INSERT INTO authorities(username,authority) VALUES ('admin', 'ROLE_ADMIN');
``` 

``` java
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired DataSource dataSource;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.jdbcAuthentication().dataSource(dataSource);
  }

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    // ...
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // ...
  }
}
```

Run:

``` sh
mvn spring-boot:run
```
