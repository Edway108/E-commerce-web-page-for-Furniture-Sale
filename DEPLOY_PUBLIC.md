# Deploy Furnituree public

## Recommended: one Spring Boot service + one MySQL database

This prepared version serves the frontend from Spring Boot:

- `/` redirects to `/htmlpage/mainPage.html`
- frontend JS calls backend by relative URLs such as `/products`, `/auth/login`
- static frontend resources are allowed by Spring Security
- Dockerfile uses Java 21, matching `pom.xml`

## Required environment variables

Set these on your hosting service:

```env
APP_NAME=furnituree
DB_URL=jdbc:mysql://HOST:PORT/DATABASE_NAME
DB_USERNAME=DATABASE_USER
DB_PASSWORD=DATABASE_PASSWORD
JWT_SECRET=replace-with-a-long-random-secret-at-least-32-characters
JWT_EXPIRATION=86400000
```

Do not commit real passwords or secrets.

## Docker deploy

Build locally:

```bash
docker build -t furnituree .
```

Run locally with a public/remote MySQL database:

```bash
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://HOST:PORT/DATABASE_NAME" \
  -e DB_USERNAME="DATABASE_USER" \
  -e DB_PASSWORD="DATABASE_PASSWORD" \
  -e JWT_SECRET="replace-with-a-long-random-secret-at-least-32-characters" \
  furnituree
```

Open:

```text
http://localhost:8080/
```

## Railway / Render style deploy

1. Push this folder to GitHub.
2. Create a MySQL database on your platform.
3. Create a web service from the GitHub repository.
4. Choose Docker deployment if asked.
5. Set the environment variables above.
6. Deploy.
7. Open the service URL. It should load `/htmlpage/mainPage.html`.

## Notes

- The database is created/updated by Hibernate because `spring.jpa.hibernate.ddl-auto=update`.
- Demo users are seeded on startup by `DataSeeder`: `admin/123456`, `manager/123456`, `user/123456`.
- For production, change demo passwords and use a strong JWT secret.
