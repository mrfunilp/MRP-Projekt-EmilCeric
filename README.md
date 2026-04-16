# MRP Projekt – Media Rating Platform

Backend project for managing media entries (movies, series, games) with authentication, ratings, favorites, profiles, and a leaderboard.

## Features

- User registration, login, logout
- Token-based authenticated endpoints
- Create/read/update/delete media entries
- Search media by filters
- Rate media (1–5 stars), like ratings, confirm comments
- Favorite/unfavorite media
- User profile and leaderboard

## Tech Stack

- Java 21
- Maven
- PostgreSQL
- Docker Compose (for local DB)
- JUnit 4 tests

## Project Structure

- `src/main/java/at/technikum` – application and server code
- `src/test/java/at/technikum` – unit tests
- `Database/schema.sql` – database schema
- `docker-compose.yml` – local PostgreSQL container
- `MRP Tests.postman_collection.json` – API test collection
- `Dokumentation.pdf` – additional documentation

## Prerequisites

- JDK 21
- Maven 3.9+
- Docker + Docker Compose

## Setup

1. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

2. If schema is not loaded automatically, import it manually:

   ```bash
   docker exec -i <postgres-container-name> psql -U mediauser -d mediadb < Database/schema.sql
   ```

## Run the Application

```bash
mvn compile
mvn exec:java -Dexec.mainClass="at.technikum.Main"
```

Server starts on:

`http://localhost:8080`

## Run Tests

```bash
mvn test
```

> Note: In some environments, build/test can fail with `invalid target release: 21` if Java 21 is not installed.

## Authentication

Use the token returned by `POST /users/login` in the `Authorization` header.

Accepted formats:

- `Bearer <token>`
- `<token>`

## API Overview

### User Endpoints

- `POST /users/register`
- `POST /users/login`
- `POST /users/logout`
- `GET /users/profile`
- `GET /users/{username}/profile`
- `GET /leaderboard`

### Media Endpoints

- `POST /media`
- `GET /media`
- `GET /media/{id}`
- `PUT /media/{id}`
- `DELETE /media/{id}`
- `POST /media/search`

### Favorites

- `GET /favorites`
- `POST /media/{id}/favorite`
- `DELETE /media/{id}/favorite`
- `GET /media/{id}/favorite`

### Ratings

- `POST /ratings`
- `PUT /ratings/{id}`
- `DELETE /ratings/{id}`
- `POST /ratings/{id}/like`
- `POST /ratings/{id}/confirm`
- `GET /media/{id}/ratings`

## Data Model Notes

- `MediaType` values: `MOVIE`, `SERIES`, `GAME`
- Rating stars must be between `1` and `5`

## API Testing

You can test all major flows with:

- `MRP Tests.postman_collection.json`

Base URL in Postman:

- `http://localhost:8080`

Some collection variables (e.g. token/media id) may need manual update depending on run order.
