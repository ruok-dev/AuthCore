# 🛡️ AuthCore - Centralized Authentication Service

AuthCore is a production-grade authentication and authorization server built with **Java 21**, **Spring Boot 3**, and **Spring Security**. It implements a robust architecture for managing users, roles, and secure token-based authentication using **JWT (JSON Web Tokens)** with **RS256** signing.

---

## 🚀 Features

- **OAuth2 & JWT**: Secure authentication using RSA encryption (RS256).
- **Refresh Token Rotation**: Enhanced security against token theft.
- **Double Persistence**: 
  - **PostgreSQL**: Reliable storage for user data and credentials.
  - **Redis**: High-performance storage for active sessions and refresh token management.
- **Dockerized**: Fully containerized environment for easy deployment.
- **OpenAPI/Swagger**: Built-in interactive API documentation.
- **Hexagonal-ish Architecture**: Clean separation between domain logic and infrastructure.

---

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.x
- **Security**: Spring Security, JWT (JJWT)
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Containers**: Docker & Docker Compose

---

## 🚦 Getting Started

### Prerequisites

- [Docker](https://www.docker.com/get-started) & [Docker Compose](https://docs.docker.com/compose/install/)
- JDK 21 (for local development)

### Running with Docker

The easiest way to start the entire stack is using Docker Compose:

```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`.

---

## 📖 API Documentation

Once the application is running, you can access the Swagger UI to explore and test the endpoints:

🔗 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### Main Endpoints

- `POST /auth/register`: Create a new user account.
- `POST /auth/login`: Authenticate and receive Access & Refresh tokens.
- `POST /auth/refresh`: Obtain a new Access Token using a valid Refresh Token.

---

## ⚙️ Environment Variables

You can customize the application behavior using the following variables in your `.env` file or environment:

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_PASSWORD` | `postgres` | PostgreSQL password |
| `REDIS_PASSWORD` | `redis_pass` | Redis password |
| `JWT_PRIVATE_KEY` | `classpath:certs/private.pem` | Path to RSA Private Key |
| `JWT_PUBLIC_KEY` | `classpath:certs/public.pem` | Path to RSA Public Key |

---

## 🛡️ Security Note

This project uses **RSA256**. Make sure to generate your own key pairs for production use:

```bash
# Generate private key
openssl genrsa -out private.pem 2048
# Extract public key
openssl rsa -in private.pem -pubout -out public.pem
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
Created with ❤️ by [ruok-dev](https://github.com/ruok-dev)
