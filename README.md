# Events User Service

A Kotlin-based REST API service for user management and authentication, built with Ktor and PostgreSQL.

## Features

- User registration and authentication with JWT tokens
- User CRUD operations (Create, Read, Update, Delete)
- Password hashing with BCrypt
- PostgreSQL database integration
- Docker support for containerized deployment

## Prerequisites

- Java 17 or higher
- PostgreSQL database
- Docker (optional, for containerized deployment)

## Building the Project

### Using Gradle

Build the project with the following command:

```bash
./gradlew build
```

To run tests:

```bash
./gradlew test
```

To run the application locally:

```bash
./gradlew run
```

## Docker Deployment

### Building the Docker Image

```bash
docker build -t events-user-service .
```

### Running the Container

```bash
docker run -p 8080:8080 \
  -e DEPLOYMENT_PORT=8080 \
  -e DATASOURCE_URL=jdbc:postgresql://postgres:5432/eventos \
  -e DATASOURCE_USERNAME=postgres \
  -e DATASOURCE_PASSWORD=password \
  -e DATASOURCE_DRIVER=org.postgresql.Driver \
  events-user-service
```

### Running with Docker Compose

Create a `docker-compose.yml` file:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: eventos
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  user-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      DEPLOYMENT_PORT: 8080
      DATASOURCE_URL: jdbc:postgresql://postgres:5432/eventos
      DATASOURCE_USERNAME: postgres
      DATASOURCE_PASSWORD: password
      DATASOURCE_DRIVER: org.postgresql.Driver
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Then run:

```bash
docker-compose up
```

## Environment Variables

Configure these environment variables when running the application:

| Variable | Description | Example |
|----------|-------------|---------|
| `DEPLOYMENT_PORT` | Server port | `8080` |
| `DATASOURCE_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/eventos` |
| `DATASOURCE_USERNAME` | Database username | `postgres` |
| `DATASOURCE_PASSWORD` | Database password | `password` |
| `DATASOURCE_DRIVER` | JDBC driver class | `org.postgresql.Driver` |

## API Endpoints

### Authentication

**Register a new user**

```bash
POST /auth/register
Content-Type: application/json

{
  "nome": "John Doe",
  "email": "john@example.com",
  "senha": "password123",
  "cpf": "12345678901",
  "telefone": "11999999999"
}
```

**Login**

```bash
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "senha": "password123"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Users (Requires Authentication)

All user endpoints require a Bearer token in the Authorization header:

```
Authorization: Bearer <token>
```

**Get all users**

```bash
GET /usuarios
```

**Get user by ID**

```bash
GET /usuarios/{id}
```

**Update user**

```bash
PUT /usuarios/{id}
Content-Type: application/json

{
  "nome": "Jane Doe",
  "email": "jane@example.com",
  "senha": "newpassword123",
  "cpf": "12345678901",
  "telefone": "11999999999"
}
```

**Delete user**

```bash
DELETE /usuarios/{id}
```

## Project Structure

```
src/main/kotlin/
├── Application.kt              # Main application entry point
├── model/
│   ├── Usuario.kt              # User data model and database table definition
│   └── LoginRequest.kt          # Login request model
├── repository/
│   ├── DatabaseFactory.kt       # Database connection setup
│   ├── UsuarioRepository.kt     # User database operations
│   └── JwtService.kt            # JWT token generation and verification
└── routes/
    ├── AuthRoutes.kt            # Authentication endpoints
    └── UsuarioRoutes.kt         # User CRUD endpoints
```


