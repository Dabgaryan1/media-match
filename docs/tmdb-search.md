# TMDB movie search

The backend searches TMDB without saving search results to PostgreSQL. Existing local media endpoints are unchanged. This first integration supports movies only; TV, books, games, and importing results into lists are separate next steps.

## Local setup

Set `TMDB_READ_ACCESS_TOKEN` in the environment of the backend process, using the API Read Access Token from your TMDB account settings. In your IDE, add it to the backend run configuration's environment variables and restart the backend. Do not add it to frontend `VITE_` variables or commit it. Spring does not automatically load a root `.env` file.

`application.properties` maps the variable to `tmdb.read-access-token`. When unset, the rest of the app can still start; external searches return `503` with `Media search is not configured`.

## Test in Postman

- Method: `GET`
- URL: `http://localhost:8080/media/search?query=Inception&page=1`
- Authorization: Bearer Token, using your **MediaMatch login JWT**. The backend supplies the separate TMDB credential when contacting TMDB.
- Body: none

The response contains `page`, `totalPages`, `totalResults`, and `results`. Each result contains `externalSource` (`TMDB`), `externalId`, `title`, `description`, `posterPath`, `releaseDate`, and `mediaType` (`MOVIE`). `posterPath` is a TMDB image path, not a complete URL. Poster paths can be null and release dates can be empty. External IDs are not local database IDs and cannot yet be passed to the existing add-to-list endpoint.

Queries must contain 1–200 characters after trimming. Pages must be 1–500 and default to 1. No matches produce an empty results array. Missing/invalid MediaMatch authentication returns `401`; invalid input returns `400`; missing provider configuration and upstream failures return `503` with a safe message. Network calls have connection and read timeouts.

## Verification

Run `./mvnw.cmd clean compile` and `./mvnw.cmd test`. The TMDB service tests mock HTTP responses; no real TMDB token is needed. The application context test still requires the project's usual database/JWT environment setup.

Provider references: [movie search](https://developer.themoviedb.org/reference/search-movie), [authentication](https://developer.themoviedb.org/docs/authentication-application), and [attribution requirements](https://developer.themoviedb.org/docs/faq). Include the required TMDB attribution when exposing the catalog in the frontend.
