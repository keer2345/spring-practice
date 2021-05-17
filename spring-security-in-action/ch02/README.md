**Chapter
02 [Hello Spring Security](https://livebook.manning.com/book/spring-security-in-action/chapter-2/)**

> kBuCA5An <> sharklasers.com / 123456

# Starting with the first project

Spring Security dependencies for our first web app:

``` xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Craete a controller:

``` java
@RestController
public class HelloController {

  @GetMapping("/hello")
  public String hello() {
    return "Hello!";
  }
}
```

Once you run the application with `mvn spring-boot:run`, besides the other lines in the console, you
should see something that looks similar to this:

``` sh
Using generated security password: 93a01cf0-794b-4b98-86ef-54860f36f7f3
```

# Which are the default configurations?

![](https://drek4537l1klr.cloudfront.net/spilca/Figures/CH02_F02_Spilca.png)

In this figure, you can see that

1. The authentication filter delegates the authentication request to the authentication manager and,
   based on the response, configures the security context.
1. The authentication manager uses the authentication provider to process authentication.
1. The authentication provider implements the authentication logic.
1. The user details service implements user management responsibility, which the authentication
   provider uses in the authentication logic.
1. The password encoder implements password management, which the authentication provider uses in
   the authentication logic.
1. The security context keeps the authentication data after the authentication process.

In the following paragraphs, I’ll discuss these autoconfigured beans:

- `UserDetailsService`
- `PasswordEncoder`

We’ll discuss more details about the implementation of this object in chapter 4. For now, you should
1be aware that a `PasswordEncoder` exists together with the default `UserDetailsService`. When we
replace the default implementation of the `UserDetailsService`, we must also specify
a `PasswordEncoder`.

# Overriding default configurations

In this section, you’ll learn how to configure a `UserDetailsService` and a `PasswordEncoder`. These
two components take part in authentication, and most applications customize them depending on their
requirements.

## Overriding the UserDetailsService component

In this chapter, we aren’t going to detail the implementations provided by Spring Security or create
our own implementation just yet. I’ll use an implementation provided by Spring Security, named
`InMemoryUserDetailsManager`. With this example, you’ll learn how to plug this kind of object into
your architecture.

An `InMemoryUserDetailsManager` implementation isn’t meant for production-ready applications, but
it’s an excellent tool for examples or proof of concepts. In some cases, all you need is users. You
don’t need to spend the time implementing this part of the functionality. In our case, we use it to
understand how to override the default `UserDetailsService` implementation.

`config.ProjectConfig.java`:

``` java
@Configuration
public class ProjectConfig {
  @Bean
  public UserDetailsService userDetailsService() {
    var userDetailsService = new InMemoryUserDetailsManager();
    return userDetailsService;
  }
}
```

Next, we need to:

1. Create at least one user who has a set of credentials (username and password).
1. Add the user to be managed by our implementation of `UserDetailsService`.
1. Define a bean of the type `PasswordEncoder` that our application can use to verify a given
   password with the one stored and managed by `UserDetailsService`.

``` java
package com.spring.securityInAction.ch02.config;

// ...

@Configuration
public class ProjectConfig {
  @Bean
  public UserDetailsService userDetailsService() {
    var userDetailsService = new InMemoryUserDetailsManager();

    var user = User.withUsername("john").password("123456").authorities("read").build();
    userDetailsService.createUser(user);

    return userDetailsService;
  }
}
```

When using the default `UserDetailsService`, a `PasswordEncoder` is also auto-configured. Because we
overrode `UserDetailsService`, we also have to declare a `PasswordEncoder`.

Otherwise, the result of the call in the app’s console is:

``` shell
ispatcherServlet] in context with path [] threw exception

java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null"
	at org.springframework.security.crypto.password.DelegatingPasswordEncoder$
	UnmappedIdPasswordEncoder.matches(DelegatingPasswordEncoder.java:254) 
	~[spring-security-core-5.5.0-SNAPSHOT.jar:5.5.0-SNAPSHOT]
```

To solve this problem, we can add a `PasswordEncoder` bean in the context, the same as we did with
the `UserDetailsService`. For this bean, we use an existing implementation of `PasswordEncoder`:

``` java
@Configuration
public class ProjectConfig {

  @Bean
  public UserDetailsService userDetailsService() {
    var userDetailsService = new InMemoryUserDetailsManager();

    var user = User.withUsername("john").password("123456").authorities("read").build();
    userDetailsService.createUser(user);

    return userDetailsService;
  }

  @Bean
  @Deprecated
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }
}
```