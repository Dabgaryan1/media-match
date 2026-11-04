# MediaMatch Codex Instructions

## Project Overview

MediaMatch is a full-stack media discovery and tracking application with recommendations planned.

This state summary was checked against the repository on November 3, 2026. Treat the code and build files as the source of truth when this document or the README becomes outdated.

Backend stack:

- Java 21
- Spring Boot 4.1.0
- Spring Security
- JWT authentication through Spring OAuth2 Resource Server
- Spring Data JPA / Hibernate and Jakarta Validation
- PostgreSQL
- Maven

Frontend:

- React 19 and TypeScript
- Vite
- React Router
- React Icons
- Plain CSS alongside page components
- npm and Oxlint

The backend supports:

- create accounts and log in
- create media lists
- add movies, shows, books, and games to lists
- maintain a personal media library
- track media status
- rate media
- favorite media
- comment on media lists
- search TMDB for movies with pagination

The React frontend currently implements home, registration, login, dashboard, and a My Lists MVP. Backend capabilities are not all exposed in the UI yet. Recommendations are not implemented.

## Architecture

MediaMatch has a monolithic Spring Boot backend and a separate React frontend in `frontend/`.

Backend source is under `src/main/java/com/danielabgaryan/mediamatch/`; backend tests are under `src/test/java/com/danielabgaryan/mediamatch/`. Frontend routes are declared in `frontend/src/App.tsx`, and pages live under `frontend/src/pages/`.

Backend packages:

- `controller` — HTTP/API layer
- `service` — business logic and authorization
- `repository` — Spring Data JPA repositories
- `model` — JPA entities and enums
- `dto` — request and response DTOs
- `exception` — custom exceptions and global exception handling
- `config` — Spring Security, CORS, JWT, password, and TMDB HTTP client configuration

Keep business logic in services rather than controllers.

Controllers should primarily:

- receive requests
- validate request DTOs
- extract authenticated identity when needed
- call services
- convert entities into safe response DTOs

Repositories should focus on data access only.

## Authentication and Authorization

Authentication uses JWT.

The JWT subject is the authenticated user's email.

Controllers may obtain the authenticated user's email with:

```java
String email = authentication.getName();
```

Do not perform an additional database lookup solely to convert the JWT email into a `User` unless the operation actually requires the `User` entity.

Do not accept a user ID from the client when ownership can be derived from the authenticated JWT.

For user-owned resources:

- `POST` — derive the owner from the JWT
- `GET` — do not require ownership unless the resource is intentionally private
- `PUT` / `DELETE` — verify ownership in the service layer

Examples of user-owned resources include:

- MediaList
- UserMedia
- Comment
- User account modifications

Shared/social resources may be readable by authenticated users even if they do not own them.

Authentication and authorization are different:

- Authentication determines who the user is.
- Authorization determines whether that user is allowed to perform an action.

Use these HTTP semantics:

- `400 Bad Request` — invalid request/input
- `401 Unauthorized` — missing or invalid authentication
- `403 Forbidden` — authenticated user does not own or cannot access the resource
- `404 Not Found` — resource does not exist
- `409 Conflict` — duplicate or conflicting resource
- `503 Service Unavailable` — external media search is unconfigured or its provider fails

Ownership failures should throw:

```java
ForbiddenException
```

Do not use `InvalidRequestException` for ownership failures.

## Password Security

Passwords are hashed using Spring Security's:

```java
BCryptPasswordEncoder
```

Never:

- store raw passwords
- return password hashes through an API
- log raw passwords
- expose `passwordHash`
- implement custom password hashing or cryptography

JWT is used for authentication after login and is not a replacement for password hashing.

Login failures should not reveal whether an email exists.

For example, use the same generic error message for:

- nonexistent email
- incorrect password

Example:

```text
Invalid email or password
```

## JWT Rules

JWT tokens are signed using the configured application secret.

The JWT currently includes:

- subject = user email
- user ID claim
- username claim
- issued-at time
- expiration time

JWTs currently expire after approximately one hour.

Do not place sensitive information such as passwords or password hashes inside JWT claims.

## DTO Rules

Do not return `User` entities directly from controllers.

Use:

```java
UserResponse
```

whenever user information is exposed through the API.

Never expose:

```java
passwordHash
```

Use response DTOs when an entity contains nested `User` data.

Current safe response DTOs include:

- `UserResponse`
- `MediaListResponse`
- `UserMediaResponse`
- `CommentResponse`
- `LoginResponse`
- `MediaSearchResponse`

`MediaListResponse` currently contains only `id`, `name`, `description`, and safe user information. It does not include media items. The frontend treats absent media data as unavailable, not as an empty list. Extend the response safely when implementing list contents; do not return raw entities to fill this gap.

Request DTOs should use Jakarta validation annotations where appropriate.

Examples:

```java
@NotBlank
@NotNull
@NotEmpty
@Size
@Email
@Min
@Max
```

Avoid requiring user IDs in create DTOs when the authenticated user can be determined from the JWT.

For example:

- `CreateMediaListRequest` should not contain `userId`
- `CreateUserMediaRequest` should not contain `userId`
- `CreateCommentRequest` should not contain `userId`

If a request field intentionally has a default value, do not add unnecessary validation.

For example, `favorite` currently uses primitive `boolean`, where omitted values intentionally default to `false`.

## Response Safety

Before returning an object through a controller, consider whether it contains:

- `User`
- `passwordHash`
- other internal or sensitive fields

Do not return raw entities containing sensitive nested data.

If user information needs to be included, map it to `UserResponse`.

Safe response mappings should remain lightweight and should not introduce unnecessary database queries.

## Coding Style

Prefer simple, readable implementations over unnecessary abstraction.

Do not introduce additional database queries solely for architectural cleanliness.

Efficiency matters.

Prefer the more efficient implementation unless an alternative provides a substantial readability or maintainability benefit.

Do not create helper or service abstractions for one-line operations such as:

```java
authentication.getName()
```

Avoid overengineering.

Do not introduce new patterns, abstractions, layers, or libraries unless they provide a clear benefit.

Follow existing package and naming conventions.

Use normal Java naming conventions:

```java
userMedia
```

instead of:

```java
usermedia
```

Keep methods focused.

Comments should explain WHY something is done, not simply restate obvious code.

Example of a useful comment:

```java
// Prevent duplicate entries for the same user and media combination
```

Avoid comments that simply repeat a method name or obvious statement.

## Service Layer Rules

Business logic belongs in services.

Authorization for existing user-owned resources should also be enforced in services.

For modification operations:

1. retrieve the resource
2. verify ownership
3. modify the resource
4. save the resource

Do not modify an entity before verifying ownership.

Example:

```java
MediaList mediaList = getMediaListById(mediaListId);

verifyOwnership(mediaList, email);

mediaList.setName(name);
mediaList.setDescription(description);

return mediaListRepository.save(mediaList);
```

GET methods should not perform ownership checks unless the resource is intentionally private.

List deletion currently verifies ownership, removes associated comments, and deletes the list within one transaction. Preserve this behavior so comments do not prevent list deletion.

## Repository Rules

Use Spring Data JPA derived queries where appropriate.

Do not keep unused repository methods.

Before deleting a repository method:

- search for references
- verify no service/controller/test uses it
- then remove it

Prefer `existsBy...` methods when only existence is needed rather than retrieving an entire entity.

Do not add custom queries unless derived queries are insufficient or inefficient.

## Model Rules

Keep database constraints consistent with request validation.

For example, if a request allows a maximum of 300 characters, the corresponding database column should not be limited to 200.

Use database constraints for important invariants when practical.

Example:

```java
@UniqueConstraint(columnNames = {"user_id", "media_id"})
```

may be used to prevent duplicate user-media relationships in addition to service-level checks.

Use:

```java
@Enumerated(EnumType.STRING)
```

for enums stored in the database.

Prefer string enum storage over ordinal values.

## Error Handling

Current custom exceptions:

```java
ResourceNotFoundException
DuplicateResourceException
InvalidRequestException
ForbiddenException
ExternalServiceException
```

Expected mappings:

```text
ResourceNotFoundException  -> 404
DuplicateResourceException -> 409
InvalidRequestException    -> 400
ForbiddenException         -> 403
ExternalServiceException   -> 503
```

Validation failures should return:

```text
400 Bad Request
```

with field-specific validation messages.

Keep exception handling centralized in:

```java
GlobalExceptionHandler
```

Custom exception responses currently contain plain-text messages; DTO validation errors contain a JSON map of field names to messages. Frontend error handling must account for these response formats.

## Development Style

For new domain features, prefer vertical slices.

Suggested order:

1. model
2. repository
3. service
4. controller
5. DTO
6. tests

Implement one feature end-to-end before starting another large feature when practical.

For cross-cutting changes such as:

- authentication
- authorization
- validation
- error handling

it is acceptable to update multiple existing layers together.

Avoid large architectural rewrites unless explicitly requested.

When replacing old functionality, remove obsolete code rather than leaving both implementations.

Before deleting methods or classes, search for references first.

Do not preserve dead code "just in case."

## Implementation Mode

The priority is completing a usable project efficiently. Tutoring is no longer the default.

- For requested changes, directly edit files and complete the implementation, including relevant backend/frontend integration and verification.
- Do not stop at hints, plans, or partial scaffolding when the user has asked for working functionality.
- Make reasonable implementation decisions using existing conventions. Ask for clarification only when missing information materially affects the result or blocks progress.
- Keep changes focused on the requested outcome. Prefer the smallest complete solution over speculative features, new abstractions, or broad rewrites.
- Preserve security, authorization, response safety, and meaningful testing while prioritizing delivery.
- Explain what changed, how it was checked, and any remaining limitations concisely.
- If the user specifically asks for an explanation, review, plan, or hint, honor that scope.

This mode does not authorize unrelated features or override the Git restrictions below.

## Performance Preferences

Prefer efficiency and performance over minor architectural purity.

Do not add extra database calls just to make code appear cleaner.

If two approaches are similarly readable, prefer the one with fewer unnecessary database operations.

A less efficient architecture is acceptable only when it provides a substantial readability, maintainability, correctness, or security benefit.

## Git

Do not commit changes unless explicitly asked.

Do not push changes unless explicitly asked.

Do not merge feature branches into `main` unless explicitly asked.

Do not create new branches unless explicitly asked.

Preserve the developer's current branch.

Before performing destructive Git operations, confirm they are intentional.

Avoid:

```text
git reset --hard
git clean -fd
force push
```

unless explicitly requested.

Do not rewrite Git history unless explicitly requested.

## Testing

Backend controller and service tests already exist, including authorization and TMDB search coverage. Extend the relevant tests when behavior changes rather than treating automated testing as a future feature.

After meaningful backend changes, run:

```powershell
.\mvnw.cmd clean compile
```

When appropriate, run:

```powershell
.\mvnw.cmd test
```

The `@SpringBootTest` application context test requires a reachable PostgreSQL database and the backend database/JWT environment configuration. TMDB tests mock provider HTTP responses and do not need a real TMDB token. Report environment-related blockers accurately.

After meaningful frontend changes, run from `frontend/`:

```powershell
npm run build
npm run lint
```

The build includes TypeScript checking. There is currently no frontend test script or automated UI test suite. Check affected user flows in the browser when available, including loading, empty, error, and authentication-expiry states.

Documentation-only changes do not require application builds or tests; check the diff and factual accuracy instead.

Do not claim code works unless compilation or relevant tests were actually run successfully.

If tests fail:

- inspect the actual failure
- identify the root cause
- do not disable or delete tests simply to make the build pass

For API/security changes, manual Postman testing may also be appropriate.

Important authorization scenarios include:

- owner modification succeeds
- non-owner modification returns `403`
- nonexistent resource returns `404`
- missing/invalid JWT returns `401`
- readable shared/social GET endpoint succeeds for another authenticated user
- API response does not expose `passwordHash`

## Current Authorization Design

Authenticated identity comes from the JWT email.

Current ownership behavior:

### MediaList

- creation derives user from JWT
- GET endpoints are readable by authenticated users
- update requires ownership
- delete requires ownership
- add media requires ownership
- remove media requires ownership

### UserMedia

- creation derives user from JWT
- library/favorites GET endpoints are readable by authenticated users
- status update requires ownership
- rating update requires ownership
- favorite update requires ownership
- removal requires ownership

### Comment

- creation derives commenter from JWT
- comment GET endpoints are readable by authenticated users
- deletion requires ownership

### User

- registration is public
- GET by ID is authenticated but not owner-only
- GET by username is authenticated but not owner-only
- GET by email is restricted to the authenticated user
- profile update requires ownership and does not modify the password
- password update requires ownership and verification of the current password
- delete requires ownership

### Media and Genre

Media and Genre are currently shared/global resources.

They do not currently use ownership checks.

Role-based/admin authorization has not yet been implemented.

Do not introduce admin/role authorization unless explicitly requested.

## Current Security Configuration

Public endpoints currently include:

```text
/auth/**
POST /users
```

All other endpoints require authentication.

The application uses stateless JWT authentication.

Sessions should remain:

```java
SessionCreationPolicy.STATELESS
```

CSRF is currently disabled because the backend uses stateless Bearer-token authentication rather than browser cookie-based sessions.

CORS is already enabled in `SecurityConfig` for `http://localhost:5173`, with all headers and methods allowed. Preserve local frontend access; adjust explicit origins when another environment is actually introduced.

## External Movie Search

- `GET /media/search?query=Inception&page=1` requires a MediaMatch JWT and searches TMDB movies only.
- `MediaSearchController` delegates to `TmdbService`; `TmdbConfig` supplies a Spring `RestClient` with connection and read timeouts.
- Queries must contain 1–200 characters after trimming. Pages are 1–500 and default to 1.
- `MediaSearchResponse` contains pagination metadata and results with `externalSource`, `externalId`, `title`, `description`, `posterPath`, `releaseDate`, and `mediaType`.
- Results are not persisted. TMDB IDs are not local `Media` IDs and cannot be passed directly to existing add-to-list or library endpoints.
- `posterPath` is a provider image path, not a complete URL; it can be null, and release dates can be empty.
- Missing TMDB configuration and provider failures return safe `503` messages. Do not expose provider credentials or confuse provider authentication failures with a MediaMatch `401`.
- TV, book, and game provider integrations, importing search results, and the frontend movie-search flow are not implemented.

See `docs/tmdb-search.md` for setup, API behavior, Postman steps, and provider references. Follow its attribution guidance when adding the TMDB catalog to the UI.

## Local Configuration and Running

- Backend environment: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET`. The current database username/password defaults are development fallbacks, not production configuration.
- TMDB environment: `TMDB_READ_ACCESS_TOKEN`, mapped to `tmdb.read-access-token`. It is optional for startup; external search returns `503` when it is absent.
- Supply backend environment variables through the process or IDE run configuration. Spring does not automatically load a root `.env` file.
- Frontend environment: `VITE_API_URL`, normally `http://localhost:8080` for local development.
- Keep database credentials, JWT secrets, and the TMDB token out of source control, logs, and frontend `VITE_` variables.
- Run the backend from the repository root with `.\mvnw.cmd spring-boot:run`.
- Run the frontend from `frontend/` with `npm install` when needed, then `npm run dev` (normally port 5173).
- Hibernate currently uses `spring.jpa.hibernate.ddl-auto=update`; do not assume a migration system is in place.

## Current Project Direction

Remaining work includes:

- frontend search results and connecting the existing TMDB search endpoint
- importing external results into local media so they can be added to lists and libraries
- returning and displaying list contents, plus frontend list editing and media management
- frontend library/status/rating/favorite, account, and comment flows
- additional external media types/providers
- recommendation logic
- expanding existing test coverage and UI polish
- deployment

These are outstanding capabilities, not authorization to implement all of them at once. Complete the current requested feature end-to-end and avoid unrelated scope expansion.

## Current User Update Design

Profile updates and password updates use separate flows.

- Profile updates use `UpdateUserRequest` and do not accept, hash, or modify a password.
- Password updates use `UpdatePasswordRequest` with the current password and new password.
- The service verifies the current raw password with `PasswordEncoder.matches(...)` before hashing and storing the new password.
- Password hashes must never be returned in either update response.

## Known Limitations
Because the JWT subject is currently the user's email, changing an email makes the existing JWT stale. The user must log in again with the new email before making another owner-only request.

The dashboard navigates to `/search?query=...` and links to `/account` and `/recommendations`, but those routes/pages are not implemented. Do not describe those flows as complete merely because navigation exists.

My Lists supports fetching, creating, and deleting lists with expandable sections, loading/error states, and retry handling. Its media contents remain unavailable with the current response DTO, and it has no list-edit or add/remove-media UI yet.

## Frontend Notes

The frontend uses React and TypeScript. Follow the existing page/component and CSS conventions.

Current routes in `frontend/src/App.tsx`:

- `/` — home
- `/register` — account creation
- `/login` — login
- `/dashboard` — dashboard
- `/lists` — My Lists

Login and registration call the backend. Login stores `token`, `userId`, and `username` in `localStorage`; protected API requests use `Authorization: Bearer <token>`. Client-stored identity is for UI/read requests only and must never replace server-side ownership checks based on the JWT.

My Lists redirects to login for missing credentials or API `401` responses. There is not yet a shared route guard or centralized authentication provider. Preserve existing behavior and handle expired authentication in new protected flows.

Use the configured `VITE_API_URL` and existing `fetch` approach. Keep request/response types aligned with backend DTOs, distinguish unavailable data from empty data, and preserve accessible forms and responsive layouts. Do not introduce a UI framework, state-management library, or alternate frontend stack without a clear need.

## General Rule

Preserve working behavior unless a change is necessary for:

- correctness
- security
- performance
- maintainability
- the requested feature

Avoid changing unrelated code while working on a focused task.
