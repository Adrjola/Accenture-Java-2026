# Spring Security Task

Spring Boot REST API secured with Spring Security.

## Running the application

```
mvn spring-boot:run
```

## Users

| Username | Password | Role  |
|----------|----------|-------|
| user     | user     | USER  |
| admin    | admin    | ADMIN |

## Endpoints

| Method | Path              | Access                |
|--------|-------------------|-----------------------|
| GET    | /api/items/**     | Any authenticated user |
| POST   | /api/items        | ADMIN only            |
| PUT    | /api/items/**     | ADMIN only            |
| DELETE | /api/items/**     | ADMIN only            |
