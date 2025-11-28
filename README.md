# Rental Management API
A Spring Boot REST API for managing rental properties with user authentication and messaging features.
## Features
- **User Authentication**: JWT-based authentication with login/register endpoints
- **Rental Management**: Create, read, update rental properties
- **File Upload**: Support for uploading rental property images
- **Messaging**: Send messages related to rentals
- **Security**: Spring Security with JWT tokens
- **API Documentation**: Swagger/OpenAPI documentation
## Technologies
- Java 17
- Spring Boot 3.5.7
- Spring Security
- Spring Data JPA
- MySQL Database
- JWT (JSON Web Tokens)
- Maven
- Swagger/OpenAPI
## Prerequisites
- Java 17 or higher
- MySQL 8.x
- Maven 3.6+
## Setup
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd rental
   ```
2. **Configure the database**
   Create a MySQL database named `rental` and update the `.env` file:
   ```
   DB_USER=your_db_username
   DB_PASSWORD=your_db_password
   JWT_SECRET=your_jwt_secret_base64_encoded
   CORS_ALLOWED_ORIGINS=http://localhost:4200
   ```
3. **Build the project**
   ```bash
   ./mvnw clean install
   ```
4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   The application will start on `http://localhost:8080`
## API Endpoints
### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/me` - Get current user information
### Rentals
- `GET /api/rentals` - Get all rentals
- `GET /api/rentals/{id}` - Get rental by ID
- `POST /api/rentals` - Create a new rental (requires authentication)
- `PUT /api/rentals/{id}` - Update a rental (requires authentication and ownership)
### Users
- `GET /api/user/{id}` - Get user by ID
### Messages
- `POST /api/messages` - Send a message (requires authentication)
## API Documentation
Access the Swagger UI documentation at:
```
http://localhost:8080/swagger-ui.html
```
API docs JSON available at:
```
http://localhost:8080/api-docs
```
## File Uploads
Uploaded files are stored in the `uploads/` directory and are accessible via:
```
http://localhost:8080/uploads/{filename}
```
Maximum file size: 10MB
## Security
- All endpoints except `/api/auth/login` and `/api/auth/register` require JWT authentication
- Include the JWT token in the Authorization header: `Bearer <token>`
- Passwords are encrypted using BCrypt
- CORS is configured for specified origins
## Project Structure
```
src/main/java/openclassroom/com/rental/
├── config/           # Configuration classes (Security, Web)
├── controller/       # REST Controllers
├── dto/              # Data Transfer Objects
├── entity/           # JPA Entities
├── exception/        # Custom exceptions and global exception handler
├── repository/       # Spring Data repositories
├── security/         # JWT and security related classes
└── service/          # Business logic services
```
## Environment Variables
- `DB_USER` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - Base64 encoded secret key for JWT tokens
- `CORS_ALLOWED_ORIGINS` - Comma-separated list of allowed CORS origins
## Development
### Generate JWT Secret
You can use the `GenerateJwtSecret.java` utility to generate a secure JWT secret.
### Database Schema
The application uses Hibernate with `ddl-auto=update` to automatically create/update database tables.
## Error Handling
The API uses a global exception handler that returns consistent error responses:
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Authentication failure
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server errors
## License
This project is part of an OpenClassrooms training program.
