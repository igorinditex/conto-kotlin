# What is Conto?

Conto is a toy banking application, where users can sign up, create accounts, and transfer money to other accounts. Detailed requirements can be found in the file `requirements.md` in this folder.

# Introduction
The balance of an account in Conto is the sum of all transfers to that account (credit), minus the sum of all transfers from that account (debit).

In the current implementation of Conto, to find the balance of an account you call the findTransfer method in the TransferService. This method implements the balance calculation by first querying the database for all transfers to or from the account, resulting in a List of zero or more Transfer objects. Then, the balance is calculated by summing over the amounts in this list, adding to the balance when it is a credit transfer, and deducting it when it is a debit transfer.

While functionally correct, this implementation leaves a lot to be desired. Over time, as more and more transfers to and from an account occur, the list of transfers to select from the database increases. This leads to more load and slower performance of the database query, and requires more and more memory to keep the list in memory.

Furthermore, the calculation itself will take more and more time, as we have to loop over ever more objects to calculate the balance.

# Assignment

Your task is to increase the performance of balance calculation by storing the current balance of accounts in the database, and keeping this balance always up-to-date by updating it whenever a transfer to or from that account occurs.

Make sure to update the set of unit tests to reflect the new design
The integration tests should succeed without any changes to their implementation

# Code overview

The project is a Maven project with three modules:

- `conto` is the main Kotlin server application
- `conto-app` is a React application that runs in the browser and connects to the REST API offered by `conto`
- `integration-test` contains automated integration tests for `conto`

# Prerequisites

Running, debugging and editing the source code requires the following tools to be installed on your machine

- [OpenJDK 11 SDK](https://jdk.java.net/java-se-ri/11) (other JDKs version 11 or higher probably work fine too, but we test on OpenJDK 11)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (as with the JDK, other IDEs probably work fine too, we have only tested on IDEA.)
- A tool like [Postman](https://www.getpostman.com/) to easily create, edit and send custom REST calls over HTTP
- [Node.js](https://nodejs.org/en/) and [Yarn](https://yarnpkg.com/) to run the JavaScript app in `conto-app`

# Running the application

First, import the project in your IDE. (Note, this has only been tested properly in IntelliJ IDEA).

To run `conto`, find the Kotlin class `com.ximedes.conto.ContoApplication` and run it as a Kotlin application. No configuration is needed - on every startup, an empty, in-memory database is created and a default admin user (with username `admin` and password `admin`) is created. Open a browser at http://localhost:8080 to open the application.

To run `conto-app`, make sure you have installed Node.js (see above). Open your favourite command line terminal and run

`yarn install`

to install all required dependencies. This will take some time, but you only have to do it once.

To actually start the application, run `yarn start`, wait for the compilation to finish, then open a browser at http://localhost:3000.

# Running the integration tests

To run the automated integration tests, run all JUnit tests found under `src/test/kotlin` in your IDE, or from maven with `maven test`.

# Accessing the in-memory database directly

If you want to query or edit the in-memory H2 database directly, you can use the embedded H2 console located at http://localhost:8080/h2-console. The correct settings (which should be the default) are:

- Driver class: `org.h2.Driver`
- JDBC URL: `jdbc:h2:mem:conto`
- User name: `sa`
- Password: empty

# Technology stack

The `conto` technology stack consists of:

- [Spring Boot](http://docs.spring.io/spring-boot/) as base application layer
- [Spring Security](http://projects.spring.io/spring-security/) for security aspects
- [H2](http://www.h2database.com/html/main.html) as in-memory database
- [MyBatis](http://www.mybatis.org/mybatis-3/) for database access
- [Flyway](https://flywaydb.org/) for dealing with database schema migrations
- [HikariCP](https://github.com/brettwooldridge/HikariCP) for database connection pooling
- [Thymeleaf](http://www.thymeleaf.org/) for server-side HTML rendering
- [Nimbus](https://bitbucket.org/connect2id/nimbus-jose-jwt/wiki/Home) for JWT handling
- [JSoup](https://jsoup.org/) for detecting and cleaning HTML input
- [Logback](https://logback.qos.ch/) for application logging

The JS client in `conto-app` is built using:

- [React](https://facebook.github.io/react/)
- [Redux](http://redux.js.org/) for state management
- [Bootstrap](http://getbootstrap.com/) for design
