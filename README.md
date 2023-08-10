# Library

## Overview

Library is a simple CRUD application for personal management of books.

## Demonstration

[![](https://img.youtube.com/vi/nvqvLYUQD0Q/0.jpg)](https://www.youtube.com/watch?v=nvqvLYUQD0Q)

## Features

- Anonymous users can register their accounts.
- Anonymous users with an account can authenticate through the system.
- Authenticated users can manage their books.
- Authenticated users can manage their books's categories.
- Authenticated users can logout the system.

## Technologies

- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA with Hibernate
- Thymeleaf as template engine
- JUnit as test framework (yes, it has tests)
- Both H2 and MySQL as databases
- Bootstrap for styling (sorry for my front-end skills)

## Getting Started

### Prerequisites

* Java 17
* MySQL (optional)

### Installing

Clone the project

```bash
  git clone https://github.com/AugustoRavazoli/library.git
```

Go to the project directory

```bash
  cd library
```

Start the application

```bash
  ./gradlew bootRun --args="--spring.profiles.active=local"
```

The application will start at `http://localhost:8080/` using an in-memory database 
with a default user `username` with password `password` with prefilled data.

### MySQL

If you want to run this application through a real MySQL database instance, 
replace the following properties in the `application-release.yaml` file and 
run the application through the `release` profile.

```yaml
  datasource:
    url: jdbc:mysql://localhost:3306/{YOUR_DATABASE}
    username: {YOUR_USER_NAME}
    password: {YOUR_PASSWORD}
```

### Tests

The application has a lot of integration tests, use the following command to run them.

```bash
  ./gradlew test
```

## Contributing

As a personal project, I don't want to receive pull-requests, 
but feel free to open issues or making your own version.

## License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details.
