**Chapter 01 [Security today](https://livebook.manning.com/book/spring-security-in-action/chapter-1/)**

- [Official website](https://spring.io/projects/spring-security)
- [Github](https://github.com/spring-projects/spring-security/)

This chapter covers

- What Spring Security is and what you can solve by using it
- What security is for a software application
- Why software security is essential and why you should care
- Common vulnerabilities that you’ll encounter at the application level

# Spring Security: The what and the why

You won’t find a lot of alternatives to Spring Security when it comes to securing a Spring application. One alternative you could consider is [Apache Shiro](https://shiro.apache.org). It offers flexibility in configuration and is easy to integrate with Spring and Spring Boot applications. Apache Shiro sometimes makes a good alternative to the Spring Security approach.

# What is software security?

We apply security in layers, and each layer depends on those below it. In this book, we discuss Spring Security, which is a framework used to implement application-level security at the top-most level.

![](https://drek4537l1klr.cloudfront.net/spilca/Figures/CH01_F03_Spilca.png)

![](https://drek4537l1klr.cloudfront.net/spilca/Figures/CH01_F04_Spilca.png)

# Why is security important?

Here are a few fictitious examples. Think about how you would see these as a user. How can these affect the organization responsible for the software?

- A back-office application should manage the internal data of an organization but, somehow, some information leaks out.
- Users of a ride-sharing application observe that money is debited from their accounts on behalf of trips that aren’t theirs.
- After an update, users of a mobile banking application are presented with transactions that belong to other users.

# Common security vulnerabilities in web applications

An excellent start to understanding vulnerabilities is being aware of the Open Web Application Security Project, also known as [OWASP](https://www.owasp.org). At OWASP, you’ll find descriptions of the most common vulnerabilities that you should avoid in your applications. Let’s take a few minutes and discuss these theoretically before diving into the next chapters, where you’ll start to apply concepts from Spring Security. Among the common vulnerabilities that you should be aware of, you’ll find these:

- Broken authentication
- Session fixation
- Cross-site scripting (XSS)
- Cross-site request forgery (CSRF)
- Injections
- Sensitive data exposure
- Lack of method access control
- Using dependencies with known vulnerabilities

# Vulnerabilities in authentication and authorization

In this book, we’ll discuss authentication and authorization in depth, and you’ll learn several ways in which you can implement them with Spring Security.

**Authentication** represents the process in which an application identifies someone trying to use it. When someone or something uses the app, we want to find their identity so that further access is granted or not. In real-world apps, you’ll also find cases in which access is anonymous, but in most cases, one can use data or do specific actions only when identified. Once we have the identity of the user, we can process the authorization.

**Authorization** is the process of establishing if an authenticated caller has the privileges to use specific functionality and data. For example, in a mobile banking application, most of the authenticated users can transfer money but only from their account.

A user that is logged in can see their products. If the application server only checks if the user is logged in, then the user can call the same endpoint to retrieve the products of some other user. In this way, John is able to see data that belongs to Bill. The issue that causes this problem is that the application doesn’t authenticate the user for data retrieval as well.

![](https://drek4537l1klr.cloudfront.net/spilca/Figures/CH01_F05_Spilca.png)

# What is session fixation?
# What is cross-site scripting (XSS)?
