# MediaMatch

**MediaMatch** is a full-stack media discovery and tracking web application for finding, organizing, and sharing movies, TV shows, books, games, and other media.

The goal of MediaMatch is to give users one place to track what they are watching, reading, or playing while discovering new media based on the things they already enjoy.

> **Status:** 🚧 MediaMatch is currently in active development.

---

## Features

### Currently Implemented

* User account creation and authentication
* JWT-based authentication
* Secure password hashing
* User profile management
* Media and genre management
* Personal media libraries
* Track media status such as:

  * Planned
  * In Progress
  * Completed
  * Dropped
* Rate media
* Favorite media
* Create, update, search, and delete custom media lists
* Add and remove media from lists
* View media lists created by other users
* Comment system backend
* Ownership-based authorization for protected resources
* RESTful API architecture
* Responsive React frontend with:

  * Home page
  * Login page
  * Account creation page
  * User dashboard

---

## Tech Stack

### Frontend

* **React**
* **TypeScript**
* **Vite**
* **React Router**
* **React Icons**
* **CSS**

### Backend

* **Java 21**
* **Spring Boot**
* **Spring Web MVC**
* **Spring Data JPA**
* **Spring Security**
* **Spring OAuth2 Resource Server**
* **JWT Authentication**
* **Jakarta Validation**
* **Maven**

### Database

* **PostgreSQL**
* **Hibernate / JPA**

---

## Architecture

MediaMatch follows a layered backend architecture:

```text
React Frontend
      |
      | HTTP / JSON
      v
Spring REST Controllers
      |
      v
Service Layer
      |
      v
Repository Layer
      |
      v
PostgreSQL
```

The backend separates responsibilities across controllers, services, repositories, DTOs, models, security configuration, and exception handling.

```text
media-match/
├── frontend/
│   ├── public/
│   └── src/
│       ├── assets/
│       ├── pages/
│       │   ├── CreateAccountPage/
│       │   ├── DashboardPage/
│       │   ├── HomePage/
│       │   └── LoginPage/
│       ├── App.tsx
│       └── main.tsx
│
├── src/main/java/com/danielabgaryan/mediamatch/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── exception/
│   ├── model/
│   ├── repository/
│   └── service/
│
├── src/main/resources/
│   └── application.properties
│
├── docs/
├── pom.xml
└── README.md
```

---

## Core Data Model

MediaMatch is built around several related entities:

### User

Stores account information, profile information, and user-owned content.

### Media

Represents a piece of media such as a movie, show, book, or game.

### Genre

Allows media to be categorized and later used for discovery and recommendation features.

### MediaList

A user-created collection of media.

### UserMedia

Connects users with media in their personal library and stores information such as:

* Status
* Rating
* Favorite status
* Date added

### Comment

Allows users to interact with media lists created by other users.

---

## Authentication & Authorization

MediaMatch uses **Spring Security and JWT authentication**.

After logging in, the backend generates a JWT containing the authenticated user's identity. Protected API requests use this token to identify the current user.

Authorization is also enforced at the service/API level so users cannot modify resources owned by another account.

Examples include:

* Updating another user's profile
* Editing another user's media list
* Removing media from another user's list
* Modifying another user's ratings, favorites, or library entries

---

## API Overview

The backend exposes REST endpoints for the application's main resources.

| Resource       | Base Endpoint  |
| -------------- | -------------- |
| Authentication | `/auth`        |
| Users          | `/users`       |
| Media          | `/media`       |
| Genres         | `/genres`      |
| Media Lists    | `/media-lists` |
| User Libraries | `/user-medias` |
| Comments       | `/comments`    |

### Example Operations

Media lists support operations including:

```text
POST    /media-lists
GET     /media-lists/{id}
GET     /media-lists/user/{userId}
GET     /media-lists/search
PUT     /media-lists/{id}
PUT     /media-lists/{listId}/media/{mediaId}
DELETE  /media-lists/{listId}/media/{mediaId}
DELETE  /media-lists/{id}
```

User libraries support tracking and updating individual media entries:

```text
POST    /user-medias
GET     /user-medias/{id}
GET     /user-medias/user/{userId}
GET     /user-medias/user/{userId}/favorites
PUT     /user-medias/{id}/status
PUT     /user-medias/{id}/rating
PUT     /user-medias/{id}/favorite
DELETE  /user-medias/{id}
```

---

## Getting Started

### Prerequisites

Make sure you have installed:

* Java 21
* PostgreSQL
* Node.js
* npm
* Git

---

### 1. Clone the Repository

```bash
git clone https://github.com/Dabgaryan1/media-match.git
cd media-match
```

---

### 2. Create the PostgreSQL Database

Create a PostgreSQL database for MediaMatch.

For example:

```sql
CREATE DATABASE mediamatch;
```

---

### 3. Configure Database Environment Variables

The backend reads its database configuration from environment variables.

```text
DB_URL
DB_USERNAME
DB_PASSWORD
```

Example values:

```text
DB_URL=jdbc:postgresql://localhost:5432/mediamatch
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

On PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/mediamatch"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password"
```

Hibernate is currently configured to automatically update the database schema during development.

---

### 4. Run the Backend

#### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

#### macOS / Linux

```bash
./mvnw spring-boot:run
```

The backend will run on:

```text
http://localhost:8080
```

---

### 5. Run the Frontend

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

The Vite development server will normally start on:

```text
http://localhost:5173
```

---

## Development Goals

MediaMatch is being built as a full-stack application emphasizing:

* REST API design
* Relational database modeling
* Authentication and authorization
* Backend architecture
* Frontend/backend integration
* User-generated content
* Recommendation algorithms
* External API integration
* Testing
* Deployment

---

## Roadmap

Planned features include:

* [ ] Connect frontend dashboard to the complete backend API
* [ ] Search for movies, shows, books, and games
* [ ] Integrate external media APIs
* [ ] Browse media by genre and media type
* [ ] Personalized media recommendations
* [ ] Genre-based recommendation scoring
* [ ] User profile customization
* [ ] Search for other users
* [ ] View other users' libraries and lists
* [ ] Find users with similar media tastes
* [ ] Complete commenting functionality in the frontend
* [ ] Improved dashboard and library management
* [ ] Automated backend testing
* [ ] Frontend testing
* [ ] Production deployment
* [ ] Docker support

---

## Project Motivation

Most media platforms focus on a single category: movies, books, television, or games.

MediaMatch is designed around the idea that someone's interests span all of them.

Instead of maintaining separate lists across multiple services, MediaMatch aims to provide a unified profile for a user's media interests while using those interests to help them discover what to experience next.

---

## Author

**Daniel Abgaryan**

Computer Science student and software developer focused on full-stack and backend development.

---

## Disclaimer

MediaMatch is currently a portfolio project under active development. Features, API endpoints, database models, and setup instructions may change as development continues.
