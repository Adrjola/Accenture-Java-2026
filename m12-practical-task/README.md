# Shelter Starter

## Purpose

This project is a simple sample application for learning basic UI development with Java and Spring Boot. It is intended for coursework, experimentation, and local practice.

## Author

Bootcamp starter project used for coursework and local practice.

## Disclaimer

This repository contains a sample homework project created for an educational course on basic UI development. It is shared for learning purposes, does not represent an official employer product or service, and does not contain proprietary or confidential materials.

## Animal Images

The project supports local image files from static resources.

- Add custom animal photos to `src/main/resources/static/images/animals/`
- Store only the filename in the animal field, for example `Luna.jpeg`
- The app resolves that value to `/images/animals/Luna.jpeg`
- A full `http://` or `https://` image URL is also accepted and used as-is (see the seeded Rabbit/Pepper entry for a working example).

If image filename/url is missing, a fallback is selected by animal type:

- CAT -> `/images/fallback/fallback-cat.jpg`
- DOG -> `/images/fallback/fallback-dog.jpg`
- OTHER -> `/images/fallback/fallback-other.jpg`

## Security

The app is secured with Spring Security (see `SecurityConfig`). Two demo
accounts are provisioned in memory - passwords are intentionally simple for
coursework, not production use. Roles are deliberately **not** hierarchical:
an admin does not also hold `USER`, and vice versa.

| Username | Password  | Roles   |
|----------|-----------|---------|
| `user`   | `user123` | `USER`  |
| `admin`  | `admin123`| `ADMIN` |

- Browsing animal pages and reading the API (`GET`) is open to everyone.
- Creating an animal (`POST /animals`, `POST /api/animals`) requires `ADMIN`.
- Adopting an animal (`POST /api/animals/{id}/adopt`) requires `USER` -
  admins cannot adopt.
- Adopting sets the animal's `adoptionDetails` (adopter user ID + date) and
  flips its status to `ADOPTED`. Only `ADMIN` callers see the resulting
  `adoptionNote` ("adopted by {userId} on {date}") in the API response;
  everyone else just sees the bare `ADOPTED` status.
- Unauthenticated requests to a protected page are redirected to Spring
  Security's built-in default login page at `/login`.

## Post-Redirect-Get

The add-animal form uses the Post-Redirect-Get pattern: the browser sends
`POST /animals`, the app saves the animal, adds a short flash message, and
redirects to `GET /animals`. This matters because refreshing the list page
does not repeat the previous form submission and accidentally create another
animal. Spring MVC supports this flow with `RedirectAttributes` flash
attributes, which are available after the redirect and then removed.

References:
- https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/flash-attributes.html
- https://en.wikipedia.org/wiki/Post/Redirect/Get

## Animal Type Dropdown

The add-animal form loads animal types with JavaScript from
`GET /api/animal-types`. The earlier server-rendered dropdown approach is
simpler for a small Thymeleaf page, because all options are rendered together
with the form. The fetch approach is more decoupled from the template, because
the browser can reuse the same API data without changing the HTML.

## OpenAPI Documentation

Swagger UI is available at:

`http://localhost:8080/swagger-ui/index.html`

Public GET endpoints can be tested directly. To test protected POST endpoints
from Swagger UI, click **Authorize** and use the demo admin account:

- username: `admin`
- password: `admin123`

**Bonus tasks** (see the instructions in `SecurityConfig`):
- **Web:** build your own login page instead of using the default one.
- **API/Swagger:** replace HTTP Basic on `/api/**` with JWT auth, so Swagger
  UI (Task B) can authorize once with a bearer token instead of prompting
  for credentials on every call.
