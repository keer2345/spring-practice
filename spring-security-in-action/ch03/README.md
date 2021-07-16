**Chapter
03 [Managing users](https://livebook.manning.com/book/spring-security-in-action/chapter-3/)**

> kofozzll <> sharklasers.com / 123456

This chapter covers:

- Describing a user with the `UserDetails` interface
- Using the `UserDetailsService` in the `authentication` flow
- Creating a custom implementation of `UserDetailsService`
- Creating a custom implementation of `UserDetailsManager`
- Using the `JdbcUserDetailsManager` in the authentication flow

This chapter is about understanding in detail one of the fundamental roles you encountered in the first example we worked on in chapter 2--the `UserDetailsService`. Along with the `UserDetailsService`, we’ll discuss

- `UserDetails`, which describes the user for Spring Security.
- `GrantedAuthority`, which allows us to define actions that the user can execute.
- `UserDetailsManager`, which extends the `UserDetailsService` contract. Beyond the inherited behavior, it also describes actions like creating a user and modifying or deleting a user’s password.

This chapter, we have more details to discuss:

- What implementations are provided by Spring Security and how to use them
- How to define a custom implementation for contracts and when to do so
- Ways to implement interfaces that you find in real-world applications
- Best practices for using these interfaces

# Implementing authentication in Spring Security

Figure 3.1 presents the authentication flow in Spring Security. This architecture is the backbone of the authentication process as implemented by Spring Security.

Spring Security’s authentication flow. The `AuthenticationFilter` intercepts the request and delegates the authentication responsibility to the `AuthenticationManager`. To implement the authentication logic, the `AuthenticationManager` uses an authentication provider. To check the username and the password, the `AuthenticationProvider` uses a `UserDetailsService` and a `PasswordEncoder`.

![](https://drek4537l1klr.cloudfront.net/spilca/HighResolutionFigures/figure_3-1.png)

- As part of user management, we use the `UserDetailsService` and `UserDetailsManager` interfaces. The `UserDetailsService` is only responsible for retrieving the user by username. This action is the only one needed by the framework to complete authentication.
- The `UserDetailsManager` adds behavior that refers to adding, modifying, or deleting the user, which is a required functionality in most applications.
- If the app only needs to authenticate the users, then implementing the `UserDetailsService` contract is enough to cover the desired functionality. To manage the users, `UserDetailsService` and the `UserDetailsManager` components need a way to represent them.

Spring Security offers the `UserDetails` contract, which you have to implement to describe a user in the way the framework understands. In this chapter, Spring Security represents the actions that a user can do with the `GrantedAuthority` interface.

![](https://drek4537l1klr.cloudfront.net/spilca/HighResolutionFigures/figure_3-2.png)

# Describing the user

In this section, you’ll learn how to describe the users of your application such that Spring Security understands them. Learning how to represent users and make the framework aware of them is an essential step in building an authentication flow. Based on the user, the application makes a decision--a call to a certain functionality is or isn’t allowed. To work with users, you first need to understand how to define the prototype of the user in your application. In this section, I describe by example how to establish a blueprint for your users in a Spring Security application.

For Spring Security, a user definition should respect the `UserDetails` contract. The `UserDetails` contract represents the user as understood by Spring Security. The class of your application that describes the user has to implement this interface, and in this way, the framework understands it.

## Demystifying the definition of the UserDetails contract

The `UserDetails` interface in Spring Security:

```java
public interface UserDetails extends Serializable {
  String getUsername();
  String getPassword();
  Collection<? extends GrantedAuthority> getAuthorities();
  boolean isAccountNonExpired();
  boolean isAccountNonLocked();
  boolean isCredentialsNonExpired();
  boolean isEnabled();
}
```

## Detailing on the GrantedAuthority contract

As you observed in the definition of the `UserDetails` interface in last section, the actions granted for a user are called authorities.

Let’s understand the `GrantedAuthority` interface:

```java
public interface GrantedAuthority extends Serializable {
    String getAuthority();
}
```

The `SimpleGrantedAuthority` class offers a way to create immutable instances of the type `GrantedAuthority`.

```java
GrantedAuthority g1 = () -> "READ";
GrantedAuthority g2 = new SimpleGrantedAuthority("READ");
```

## Writing a minimal implementation of UserDetails

With a class named `DummyUser`, let’s implement a minimal description of a user. Instances of this class always refer to only one user, _bill_, who has the password _12345_ and an authority named _READ_.

```java
public class DummyUser implements UserDetails {

  @Override
  public String getUsername() {
    return "bill";
  }

  @Override
  public String getPassword() {
    return "12345";
  }

  // Omitted code

}
```

Implementation of the `getAuthorities()` method:

```java
public class DummyUser implements UserDetails {

  // Omitted code

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> "READ");
  }

  // Omitted code

}
```

Implementation of the last four `UserDetails` interface methods:

```java
public class DummyUser implements UserDetails {

  // Omitted code

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  // Omitted code

}
```

For a real application, you should create a class that you can use to generate instances that can represent different users. In this case, your definition would at least have the username and the password as attributes in the class, as shown in the next listing.

A more practical implementation of the `UserDetails` interface

```java
public class SimpleUser implements UserDetails {

  private final String username;
  private final String password;

  public SimpleUser(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  // Omitted code

}
```

## Using a builder to create instances of the UserDetails type

Some applications are simple and don’t need a custom implementation of the `UserDetails` interface.

The `User` class from the org.springframework.security.core.userdetails package is a simple way to build instances of the `UserDetails` type. Using this class, you can create immutable instances of `UserDetails`. You need to provide at least a username and a password, and the username shouldn’t be an empty string.

Constructing a user with the `User` builder class:

```java
UserDetails u = User.withUsername("bill")
                .password("12345")
                .authorities("read", "write")
                .accountExpired(false)
                .disabled(true)
                .build();
```

Creating the User.UserBuilder instance:

```java
User.UserBuilder builder1 = User.withUsername("bill");

UserDetails u1 = builder1
                 .password("12345")
                 .authorities("read", "write")
                 .passwordEncoder(p -> encode(p))
                 .accountExpired(false)
                 .disabled(true)
                 .build();

User.UserBuilder builder2 = User.withUserDetails(u);

UserDetails u2 = builder2.build();
```

## Combining multiple responsibilities related to the user

Defining the JPA User entity class:

```java
@Entity
public class User {

  @Id
  private Long id;
  private String username;
  private String password;
  private String authority;

  // Omitted getters and setters

}
```

The `User` class has two responsibilities:

```java
@Entity
public class User implements UserDetails {

  @Id
  private int id;
  private String username;
  private String password;
  private String authority;

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  public String getAuthority() {
    return this.authority;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> this.authority);
  }

  // Omitted code

}
```

Implementing the `User` class only as a JPA entity:

```java
@Entity
public class User {

  @Id
  private int id;
  private String username;
  private String password;
  private String authority;

  // Omitted getters and setters

}
```

The `SecurityUser` class implements the `UserDetails` contract:

```java
public class SecurityUser implements UserDetails {

  private final User user;

  public SecurityUser(User user) {
    this.user = user;
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> user.getAuthority());
  }

  // Omitted code

}
```

# Instructing Spring Security on how to manage users

## Understanding the UserDetailsService contract

```java
public interface UserDetailsService {

  UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException;
}
```

![](https://drek4537l1klr.cloudfront.net/spilca/HighResolutionFigures/figure_3-3.png)

## Implementing the UserDetailsService contract

The implementation of the `UserDetails` interface:

```java
public class User implements UserDetails {

  private final String username;
  private final String password;
  private final String authority;

  public User(String username, String password, String authority) {
    this.username = username;
    this.password = password;
    this.authority = authority;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(() -> authority);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
```

The implementation of the `UserDetailsService` interface:

```java
public class InMemoryUserDetailsService implements UserDetailsService {

  private final List<UserDetails> users;

  public InMemoryUserDetailsService(List<UserDetails> users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {

    return users.stream()
      .filter(
         u -> u.getUsername().equals(username)
      )
      .findFirst()
      .orElseThrow(
        () -> new UsernameNotFoundException("User not found")
      );
   }
}
```

`UserDetailsService` registered as a bean in the configuration class:

```java
@Configuration
public class ProjectConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails u = new User("john", "12345", "read");
    List<UserDetails> users = List.of(u);
    return new InMemoryUserDetailsService(users);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
```

The definition of the endpoint used for testing the implementation:

```java
@RestController
public class HelloController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello!";
  }
}
```

Run and result:

```
curl -u john:12345 http://localhost:8080/hello
```

```
Hello!
```

## Implementing the UserDetailsManager contract

```java
public interface UserDetailsManager extends UserDetailsService {
  void createUser(UserDetails user);
  void updateUser(UserDetails user);
  void deleteUser(String username);
  void changePassword(String oldPassword, String newPassword);
  boolean userExists(String username);
}
```

The SQL query for creating the users and authorities table:

```sql
CREATE TABLE IF NOT EXISTS `spring`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `enabled` INT NOT NULL,
  PRIMARY KEY (`id`));

CREATE TABLE IF NOT EXISTS `spring`.`authorities` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `authority` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`));

INSERT IGNORE INTO `spring`.`authorities` VALUES (NULL, 'john', 'write');
INSERT IGNORE INTO `spring`.`users` VALUES (NULL, 'john', '12345', '1');
```

Dependencies needed to develop the example project:

```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
   <groupId>mysql</groupId>
   <artifactId>mysql-connector-java</artifactId>
   <scope>runtime</scope>
</dependency>
```

You can configure a data source in the application.properties file of the project or as a separate bean. If you choose to use the application.properties file, you need to add the following lines to that file:

```
spring.datasource.url=jdbc:mysql://localhost/spring
spring.datasource.username=<your user>
spring.datasource.password=<your password>
spring.datasource.initialization-mode=always
```

Configuration

```java
public class ProjectConfig {

  @Bean
  public UserDetailsService userDetailsService(DataSource dataSource) {
    return new JdbcUserDetailsManager(dataSource);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
```

The test endpoint to check the implementation:

```java
@RestController
public class HelloController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello!";
  }
}
```

Changing `JdbcUserDetailsManager`’s queries to find the user:

```java
@Bean
public UserDetailsService userDetailsService(DataSource dataSource) {
  String usersByUsernameQuery =
     "select username, password, enabled
      ➥ from users where username = ?";
  String authsByUserQuery =
     "select username, authority
      ➥ from spring.authorities where username = ?";

      var userDetailsManager = new JdbcUserDetailsManager(dataSource);
      userDetailsManager.setUsersByUsernameQuery(usersByUsernameQuery);
      userDetailsManager.setAuthoritiesByUsernameQuery(authsByUserQuery);
      return userDetailsManager;
}
```
