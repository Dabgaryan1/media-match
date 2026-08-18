# MediaMatch Codex Instructions

## Project Overview

MediaMatch is a full-stack media tracking and recommendation application.

Backend stack:

- Java 21
- Spring Boot
- Spring Security
- JWT authentication
- PostgreSQL
- Maven

Frontend:

- React will be added later.

The application allows users to:

- create accounts and log in
- create media lists
- add movies, shows, books, and games to lists
- maintain a personal media library
- track media status
- rate media
- favorite media
- comment on media lists
- eventually receive media recommendations

## Architecture

MediaMatch is currently a monolithic Spring Boot application.

Backend packages:

- `controller` — HTTP/API layer
- `service` — business logic and authorization
- `repository` — Spring Data JPA repositories
- `model` — JPA entities and enums
- `dto` — request and response DTOs
- `exception` — custom exceptions and global exception handling
- `config` — Spring Security, JWT, and password configuration

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
```

Expected mappings:

```text
ResourceNotFoundException  -> 404
DuplicateResourceException -> 409
InvalidRequestException    -> 400
ForbiddenException         -> 403
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

## Learning Mode

The developer is using this project to learn software engineering.

Default behavior should be tutoring, not solution generation.

Unless the developer explicitly asks for the full solution:

- Do not write the final implementation for them.
- Do not directly edit project files.
- Do not generate entire methods, classes, or files that solve the task.
- Do not immediately reveal the complete answer.

Instead:

1. Identify what is wrong or what concept is relevant.
2. Explain the concept briefly.
3. Give the smallest useful hint.
4. Let the developer attempt the change.
5. Review their attempt.
6. If they are still stuck, provide a slightly stronger hint.
7. Only provide the complete solution if the developer explicitly asks for it.

Prefer guiding questions such as:

- "What value should this method return?"
- "Where is this field currently being validated?"
- "Which layer should own this logic?"
- "What information can you already get from the JWT?"

When reviewing code:

- Point to the exact problematic line or concept.
- Explain why it is wrong.
- Do not automatically rewrite it.
- Let the developer propose the fix first when practical.

Small syntax reminders are okay.

Small examples unrelated to the exact project solution are okay when they help explain a concept.

If the developer asks:
- "What's wrong with this?" — explain the problem without fixing it unless asked.
- "Give me a hint" — give only a hint.
- "How should I approach this?" — explain the steps/concepts without implementing them.
- "Fix this" or "implement this" — full implementation is allowed.
- "Give me the solution" — full solution is allowed.

When there are multiple issues, reveal them incrementally rather than dumping every solution at once.

The goal is for the developer to write and understand as much of the code as possible.

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

After meaningful backend changes, run:

```powershell
.\mvnw.cmd clean compile
```

When appropriate, run:

```powershell
.\mvnw.cmd test
```

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
- update requires ownership
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

## Current Project Direction

MediaMatch will eventually include:

- external media API integration
- recommendation logic
- React frontend
- testing and polish
- deployment

Do not prematurely build these features unless they are the current task.

## Known Follow-Up

The current user update flow combines profile updates and password updates.

This should be refactored.

Ordinary profile updates should eventually allow fields such as:

- username
- email
- bio
- profile picture URL

to change without requiring the password to be submitted again or re-hashed.

Password changes should be handled separately from normal profile updates.

Do not forget this follow-up.

Do not perform this refactor unless it is the current requested task.

## Frontend Notes

The frontend will use React.

Do not assume a vanilla HTML/CSS/JavaScript frontend.

When the React frontend is introduced, CORS configuration may be necessary if the frontend and backend run on different origins.

Do not add CORS configuration until it is actually needed.

## General Rule

Preserve working behavior unless a change is necessary for:

- correctness
- security
- performance
- maintainability
- the requested feature

Avoid changing unrelated code while working on a focused task.