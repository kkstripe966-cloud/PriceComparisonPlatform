# AI Coding Assistant Instructions for Price Comparison Platform

## Architecture Overview
This is a Spring Boot web application for takeout price comparison, structured as:
- **Backend**: Spring MVC with MyBatis ORM, JWT authentication, email verification
- **Frontend**: Static HTML/CSS/JS served from `src/main/webapp`
- **Database**: MySQL with Druid connection pooling
- **Deployment**: WAR packaging via Tomcat Maven plugin

Key components:
- `Application.java`: Spring Boot entry point
- `AppConfig.java`: Core configuration (datasource, MyBatis, transactions)
- `UserController.java`: REST API endpoints under `/api/user`
- `UserServiceImpl.java`: Business logic with password encoding and verification
- `UserMapper.java` + `UserMapper.xml`: Data access layer for user operations
- `JwtUtil.java`: Token generation/parsing utilities

## Critical Workflows
- **Build**: `mvn clean package` (creates WAR in target/)
- **Run locally**: `mvn tomcat7:run` (starts on http://localhost:8080/priceplatform)
- **Database setup**: Create MySQL database `price_comparison` with user table (see UserMapper.xml for schema)
- **Debug**: Standard Java debugging; MyBatis logs to stdout when enabled

## Project-Specific Patterns
- **Authentication**: Bearer tokens in `Authorization` header; validate with `JwtUtil.getUserIdFromToken()`
- **Password handling**: Always use `PasswordEncoder` (BCrypt) for encoding/checking
- **Verification codes**: 6-digit codes expire in 5 minutes, stored in `verification_code` table
- **Email sending**: QQ SMTP configured in `EmailConfig.java` (hardcoded credentials)
- **Entity mapping**: MyBatis camelCase enabled; XML mappers in `resources/mapper/`
- **Response format**: Controllers return `Map<String, Object>` with `success`, `message`, `data` keys
- **Validation**: Use `ValidateUtil` for phone/email format checks
- **Transactions**: `@Transactional` on service methods modifying data

## Integration Points
- **Database**: MySQL connection via Druid pool (config in application.yml)
- **Email**: JavaMail for verification codes (QQ SMTP)
- **Frontend API calls**: AJAX to `/priceplatform/api/user/*` endpoints
- **Static resources**: Served from `/static/**` classpath

## Conventions
- Package structure: `controller` → `service/impl` → `dao` → `entity`
- Configuration: Properties in `application.yml`, beans in config classes
- Error handling: Return error maps instead of exceptions in controllers
- Logging: MyBatis stdout logging for development

Reference files: [Application.java](PriceComparisonPlatform/src/main/src/main/java/com/priceplatform/Application.java), [UserController.java](PriceComparisonPlatform/src/main/src/main/java/com/priceplatform/controller/UserController.java), [AppConfig.java](PriceComparisonPlatform/src/main/src/main/java/com/priceplatform/config/AppConfig.java)</content>
<parameter name="filePath">d:\Users\stripe\Github\PriceComparisonPlatform\.github\copilot-instructions.md