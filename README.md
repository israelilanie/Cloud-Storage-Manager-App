# Cloud Storage Manager App

A secure Spring Boot web application for managing personal files, notes, and website credentials from a single authenticated dashboard. The project began as Udacity's Super*Duper*Drive cloud storage assignment and now contains a complete MVC implementation with security, persistence, Thymeleaf views, and automated browser/service tests.

## What I Found in the Project

After reviewing the application structure and source files, this is a compact but genuinely useful full-stack Java web app:

- **User authentication is implemented end-to-end** with Spring Security, a custom authentication provider, signup/login pages, CSRF-protected forms, logout, and protected application routes.
- **User passwords are salted and hashed** before storage through `HashService`, which is a great security baseline for account credentials.
- **Credential passwords are encrypted before persistence** and decrypted only when the authenticated user opens the edit modal.
- **All core user data is scoped by user ID**, so files, notes, and credentials are fetched, updated, and deleted for the signed-in owner.
- **The UI is clean and practical**: one home page with tabs for files, notes, and credentials, Bootstrap styling, modal-based edits, and clear result pages.
- **Testing is stronger than a typical starter project**: Selenium tests cover signup, login, logout, unauthorized access, note CRUD, credential CRUD, bad URLs, and large uploads; service tests verify update behavior.

## Features

### Authentication

- Sign up with first name, last name, username, and password.
- Login with Spring Security form authentication.
- Logout from the application dashboard.
- Redirect unauthenticated users away from protected pages.
- Show friendly login, signup, and error feedback.

### File Management

- Upload files to the application database.
- Reject empty uploads.
- Prevent duplicate file names for the same user.
- Download/view stored files from the dashboard.
- Delete files owned by the current user.
- Enforce a 1 MB upload limit with graceful error handling.

### Notes

- Create notes with a title and description.
- View all notes belonging to the authenticated user.
- Edit existing notes through a Bootstrap modal.
- Delete notes owned by the current user.

### Credentials

- Store website URL, username, and password entries.
- Encrypt stored credential passwords.
- Display encrypted credential values in the table.
- Open an edit modal with the decrypted password when needed.
- Update and delete credentials owned by the current user.

## Tech Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Web MVC**
- **Spring Security**
- **Thymeleaf**
- **MyBatis**
- **H2 in-memory database** configured in PostgreSQL compatibility mode
- **PostgreSQL JDBC driver** available for production-style database integration
- **Bootstrap**
- **JUnit 5**
- **Selenium WebDriver**
- **WebDriverManager**
- **Maven Wrapper**

## Architecture Overview

```text
Browser
  |
  v
Thymeleaf Templates
  |
  v
Spring MVC Controllers
  |
  v
Service Layer
  |
  v
MyBatis Mappers
  |
  v
H2 / PostgreSQL-compatible schema
```

### Main Packages

```text
src/main/java/com/udacity/jwdnd/course1/cloudstorage
├── config          # Spring Security configuration
├── controller      # MVC controllers and global exception handling
├── mapper          # MyBatis mapper interfaces and SQL statements
├── model           # Plain Java data models
├── security        # Custom authentication provider
└── services        # Business logic for users, files, notes, credentials, hashing, encryption
```

### Resource Layout

```text
src/main/resources
├── application.properties  # datasource, multipart, MyBatis, and error settings
├── schema.sql              # USERS, FILES, NOTES, and CREDENTIALS tables
├── static                  # Bootstrap, jQuery, and Popper assets
└── templates               # Thymeleaf pages
```

## Getting Started

### Prerequisites

Install the following tools:

- Java 21+
- Maven is optional because the repository includes `mvnw`
- Chrome or Chromium for Selenium browser tests

### Run the Application

From the project root:

```bash
./mvnw spring-boot:run
```

Open the app in your browser:

```text
http://localhost:8080
```

Create an account, sign in, and use the dashboard tabs to manage files, notes, and credentials.

### Run Tests

```bash
./mvnw test
```

The test suite includes:

- Spring Boot context loading
- Service-level note and credential update checks
- Selenium browser tests for authentication and CRUD flows
- Browser checks for unauthorized access, bad URLs, and large upload handling

## Configuration

The default configuration uses an in-memory H2 database:

```properties
spring.datasource.url=jdbc:h2:mem:cloudstoragedb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.sql.init.mode=always
```

Multipart upload limits are configured as:

```properties
spring.servlet.multipart.max-file-size=1MB
spring.servlet.multipart.max-request-size=1MB
```

## Database Tables

The schema defines four primary tables:

| Table | Purpose |
| --- | --- |
| `USERS` | Stores registered users, salts, and hashed passwords. |
| `FILES` | Stores uploaded file metadata and binary data. |
| `NOTES` | Stores user-owned note titles and descriptions. |
| `CREDENTIALS` | Stores user-owned website credentials with encrypted passwords. |

## Security Notes

This project already has several strong security foundations:

- Spring Security protects all routes except login, signup, static assets, and error pages.
- CSRF tokens are included in forms.
- Account passwords are salted and hashed with PBKDF2.
- Credential passwords are encrypted before database storage.
- Update/delete operations check the authenticated user's ownership.

Recommended production hardening:

1. Replace AES/ECB with an authenticated encryption mode such as AES-GCM.
2. Move encryption secrets and database credentials into environment variables or a secrets manager.
3. Add validation annotations to request models and show field-level errors in the UI.
4. Add database constraints for required fields and per-user unique file names.
5. Add rate limiting and account lockout protections for repeated login failures.
6. Use persistent storage such as PostgreSQL instead of the default in-memory H2 database.
7. Add file type scanning and storage quotas before accepting uploads in production.

## What Excited Me

The best part of this project is that it is small enough to understand quickly but complete enough to demonstrate real application engineering. It has authentication, authorization, persistence, encrypted sensitive data, browser-based tests, and a usable UI. That combination makes it a strong portfolio project and a great foundation for a more polished personal storage product.

## Improvement Roadmap

### High Impact

- Add Bean Validation (`@NotBlank`, `@Size`, `@Pattern`) to user input.
- Introduce DTOs/form objects instead of binding database models directly to web forms.
- Improve encryption by replacing `AES/ECB/PKCS5Padding` with AES-GCM and per-record IVs.
- Add database migrations with Flyway or Liquibase.
- Add a production profile for PostgreSQL.

### User Experience

- Add search and filtering for files, notes, and credentials.
- Show file size and upload date in the file table.
- Add confirmation dialogs before destructive deletes.
- Add flash messages instead of a separate result page.
- Improve mobile responsiveness and accessibility labels.

### Testing and Quality

- Add controller tests with MockMvc.
- Add mapper integration tests for SQL behavior.
- Add negative tests for cross-user access attempts.
- Add CI with Maven test execution on every pull request.
- Add static analysis and formatting checks.

### Operations

- Add Docker and Docker Compose for app + PostgreSQL startup.
- Add health checks with Spring Boot Actuator.
- Add structured logging.
- Add deployment documentation for a cloud platform.

## Useful Commands

```bash
# Run the app
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Build the project
./mvnw clean package
```

## Project Status

The application is feature-complete for the original cloud storage assignment and is ready for local development, testing, and incremental production hardening.
