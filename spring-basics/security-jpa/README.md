# [Spring Security with JPA and MySQL](https://youtu.be/TNt3GHuayXs?list=PLqq-6Pq4lTTYTEooakHchTGglSvkZAjnE)

## Dependencies

Create Spring Boot project at https://start.spring.io/ with follow dependencies:

- Spring Web
- Spring Security
- Spring Data JPA
- MySQL Driver

## Database Environment with MySQL

### Database Server

Database Server with [docker-compose](https://github.com/keer2345/docker-databases-with-adminer).

### Craete Database and User

``` sql
create database springboot_security default charset utf8 collate utf8_general_ci;
create user 'springboot'@'%' identified by 'spring123456';
grant all privileges on springboot_security.* to "springboot";
flush privileges;
```

### Database Configuration

`application.properties`:

``` properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:33060/springboot_security
spring.datasource.username=springboot
spring.datasource.password=spring123456

spring.jpa.properties.hibernate.hbm2ddl.auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect
spring.jpa.show-sql= true
```

## Java
**Add new Java files**:
- `src/main/java/com/keer/spring/securityJpa/SecurityConfiguration.java`
- `src/main/java/com/keer/spring/securityJpa/controller/HomeController.java`
- `src/main/java/com/keer/spring/securityJpa/entity/User.java`
- `src/main/java/com/keer/spring/securityJpa/repository/UserRepository.java`
- `src/main/java/com/keer/spring/securityJpa/security/MyUserDetails.java`
- `src/main/java/com/keer/spring/securityJpa/security/MyUserDetailsService.java`
  
**Modify the file**:

- `src/main/java/com/keer/spring/securityBasics/SecurityConfiguration.java`

Add `@EnableJpaRepositories(basePackageClasses = UserRepository.class)`
``` java
package com.keer.spring.securityJpa;

// import ...

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class SecurityJpaApplication {

  public static void main(String[] args) {
    SpringApplication.run(SecurityJpaApplication.class, args);
  }
}
```
``` sql
INSERT INTO `user` (`id`, `active`, `password`, `roles`, `username`) VALUES
(1,	1,	'user',	'ROLE_USER',	'user'),
(2,	1,	'admin',	'ROLE_ADMIN',	'admin');
```

**Run this project**

```
mvn spring-boot:run
```