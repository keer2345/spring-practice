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

## Overriding the endpoint authorization configuration

Using the HttpSecurity parameter to alter the configuration:

``` java
@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {

  // ...

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.authorizeRequests().anyRequest().authenticated();
  }
}
```

With a slight change, you can make all the endpoints accessible without the need for credentials:

``` java
@Override
protected void configure(HttpSecurity http) throws Exception {
  http.httpBasic();
  http.authorizeRequests().anyRequest().permitAll();
}
```

## Setting the configuration in different ways

In this section, you’ll learn alternatives for configuring `UserDetailsService`
and `PasswordEncoder`.

Setting `UserDetailsService` and `PasswordEncoder` in `configure()`:

``` java
@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {

  @Override
  @Deprecated
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    var userDetailsService = new InMemoryUserDetailsManager();
    var user = User.withUsername("john").password("123456").authorities("read").build();
    userDetailsService.createUser(user);
    auth.userDetailsService(userDetailsService).passwordEncoder(NoOpPasswordEncoder.getInstance());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.authorizeRequests().anyRequest().authenticated();
  }
}
```

We also call the `userDetailsService()` method from the `AuthenticationManagerBuilder` to register
the `UserDetailsService` instance. Furthermore, we call the `passwordEncoder()` method to register
the `PasswordEncoder`.

> **NOTE**
>
> The `WebSecurityConfigurerAdapter` class contains three different overloaded `configure()` methods.
> In listing 2.9, we overrode a different one than in listing 2.8. In the next chapters,
> we’ll discuss all three in more detail.

I recommend you avoid mixing configurations because it might create confusion. For example, the code
in the following listing could make you wonder about where the link between the `UserDetailsService`
and `PasswordEncoder` is.

Mixing configuration styles:

``` java
package com.spring.securityInAction.ch02.config;

// import ...

@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {
  @Bean
  @Deprecated
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    var userDetailsService = new InMemoryUserDetailsManager();
    var user = User.withUsername("john").password("123456").authorities("read").build();

    userDetailsService.createUser(user);
    auth.userDetailsService(userDetailsService);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.authorizeRequests().anyRequest().authenticated();
  }
}
```

Configuring in-memory user management:

``` java
@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {

  @Override
  @Deprecated
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("john")
        .password("123456")
        .authorities("read")
        .and()
        .passwordEncoder(NoOpPasswordEncoder.getInstance());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.authorizeRequests().anyRequest().authenticated();
  }
}
```

Generally, I don’t recommend this approach, as I find it better to separate and write
responsibilities as decoupled as possible in an application.

## Overriding the AuthenticationProvider implementation

As you’ve already observed, Spring Security components provide a lot of flexibility, which offers us
a lot of options when adapting these to the architecture of our applications. Up to now, you’ve
learned the purpose of `UserDetailsService` and `PasswordEncoder` in the Spring Security
architecture. And you saw a few ways to configure them. It’s time to learn that you can also
customize the component that delegates to these, the `AuthenticationProvider`.

Implementing the `AuthenticationProvider` interface:

``` java
package com.spring.securityInAction.ch02.security;

// import ...

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = String.valueOf(authentication.getCredentials());

    if ("john".equals(username) && "123456".equals(password)) {
      return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList());
    } else {
      throw new AuthenticationCredentialsNotFoundException("Error in authentication!");
    }
  }

  @Override
  public boolean supports(Class<?> authenticationType) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authenticationType);
  }
}
```

``` java
package com.spring.securityInAction.ch02.config;

// import ...

@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {
  @Autowired private CustomAuthenticationProvider authenticationProvider;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authenticationProvider);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.authorizeRequests().anyRequest().authenticated();
  }
}
```

Run and try it:

``` shell
mvn spring-boot:run
```

## Using multiple configuration classes in your project

In a production-ready application, you probably have more declarations than in our first examples.
You also might find it useful to have more than one configuration class to make the project
readable.

It’s always a good practice to have only one class per each responsibility. For this example, we can
separate user management configuration from authorization configuration.

``` java
package com.spring.securityInAction.ch02.config;

// import ...

@Configuration
public class UserManagementConfig {
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

``` java
package com.spring.securityInAction.ch02.config;

// import ...

@Configuration
public class WebAuthorizationConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.httpBasic();
    http.authorizeRequests().anyRequest().authenticated();
  }
}
```

# Summary