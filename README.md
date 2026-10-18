# Linkly — Secure URL Shortener with Click Analytics

Linkly is a full-stack URL shortener that creates compact links and provides click analytics for registered users.

## Features
- User registration and login with JWT authentication
- Short URL generation and original URL redirection
- Dashboard to manage and delete shortened links
- Click tracking and date-based analytics
- Paginated URL listing

## Tech Stack
- **Frontend:** React, Tailwind CSS, Material UI
- **Backend:** Java, Spring Boot, Spring Security
- **Database:** MySQL / PostgreSQL
- **Testing & CI:** JUnit, Mockito, GitHub Actions

## Live Demo
[Open Linkly](https://linkly-clicks.netlify.app/)

## Run Locally

**Prerequisites:** Java 25, Node.js, npm, and a configured database.

1. Clone the repository.
2. Configure the backend environment variables.
3. Start the backend:

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

4. In another terminal, start the frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

Configure `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `DATABASE_DIALECT`, `JWT_SECRET`, and `FRONTEND_URL` in the backend environment. Configure `VITE_BACKEND_URL` in the frontend if that is the variable used by your current frontend configuration.

## CI
GitHub Actions runs backend tests and builds the frontend on pushes and pull requests to the configured branches.

## Screenshots
<img width="1763" height="2177" alt="Screenshot_18-10-2026_181545_linkly-clicks netlify app" src="https://github.com/user-attachments/assets/45b5271b-29ef-40c1-b7e7-397a5a3371c5" />
<img width="1910" height="872" alt="screenshot-1792327676133" src="https://github.com/user-attachments/assets/5f02f6e6-6730-45ea-ae86-3d797b812ce6" />
