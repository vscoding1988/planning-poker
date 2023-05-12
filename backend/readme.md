# Planning Poker Backend
This is the backend component for the Planning Poker web application. It is built using the Spring Boot framework and provides a RESTful API for managing planning sessions and user authentication.

## Getting Started
To run the Planning Poker backend, you will need to have the following software installed on your system:

- Java SE Development Kit (JDK)
- Apache Maven
- MySQL Server (or any other DB, without configuration it will use in memory DB)
Once you have installed the required software, you can run the backend using the following command:

```shell
mvn spring-boot:run
```

This will start the backend on http://localhost:8080.

## API Reference
The Planning Poker backend provides the following APIs:

### Authentication
POST /api/auth/signup: Register a new user account
POST /api/auth/signin: Authenticate a user and generate a JWT token
### Planning Sessions
POST /api/sessions: Create a new planning session
GET /api/sessions/{sessionId}: Retrieve a specific planning session
PUT /api/sessions/{sessionId}: Update an existing planning session
DELETE /api/sessions/{sessionId}: Delete a planning session
For more details on each API, please refer to the Swagger documentation provided by the backend.

## Database Configuration
The Planning Poker backend uses MySQL as its database. You will need to configure the database connection by updating the application.properties file with your database credentials:

# Copy code
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/planning_poker?useSSL=false
spring.datasource.username=<your_database_username>
spring.datasource.password=<your_database_password>
```
## Contributing
If you'd like to contribute to the Planning Poker backend, please create a pull request with your changes. All contributions are welcome!

## License
The Planning Poker backend is licensed under the MIT License. See LICENSE for more information.
