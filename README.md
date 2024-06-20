# Ecommerce Application with RabbitMQ and Kafka

This project is a RESTful API for an E-Commerce application developed using Spring Boot, PostgreSQL, RabbitMQ, and Kafka. The application allows users to manage products, transactions, and provides functionalities such as user authentication, product recommendations, and performance testing.

## Features

### User Management
- Create users
- Update users
- Delete users
- Retrieve a specific user by ID
- Retrieve all users with optional pagination
- User authentication and JWT-based authorization

### Product Management
- Create product entries
- Update product entries
- Delete product entries
- Retrieve a specific product entry by ID
- Retrieve all product entries with optional pagination

### Transaction Management
- Create transactions
- Process transactions
- Retrieve a specific transaction by ID
- Retrieve all transactions with optional pagination
- Retrieve transactions by user

### Product Recommendations
- Recommend products based on similar users' purchase history using Collaborative Filtering
- Handle product recommendation requests via Kafka

### Performance Testing
- Performance test for RabbitMQ and Kafka messaging
- Measure throughput and latency for messaging systems

### Error Handling
- Handle errors gracefully with custom exception handling
- Return data in a structured JSON format

## Entity Relationship Diagram (ERD)

![](https://dwidi.com/wp-content/uploads/2024/06/ERD-Ecommerce-RabbitMQ-Kafka.png)

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL
- RabbitMQ
- Kafka
- Maven
- Spring Boot
- Faker (For Dummy Data)

### Installation

1. Clone the repository:
   ```bash
   gh repo clone dwididit/ecommerce-rabbitmq-kafka
   cd ecommerce-rabbitmq-kafka/
   ```
2. Create `application-dev.properties` file
   ```bash
   # Database configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/database_name
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   
   # Server port
   server.port=9090
   
   # Logging configuration
   logging.level.root=INFO
   logging.level.dev.dwidi=DEBUG
   logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n
   logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
   logging.file.name=logs/application.log
   
   # CORS Configuration
   url.frontend=http://your-url.com
   
   # Whitelist URLs
   security.whitelist.urls=/api/auth/**,/api/public/**,/api/another/**,/swagger-ui/**,/v3/api-docs/**,/v3/api-docs
   
   # Kafka configuration
   spring.kafka.bootstrap-servers=localhost:9092
   spring.kafka.consumer.group-id=group_id
   spring.kafka.consumer.auto-offset-reset=earliest
   spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
   spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
   spring.kafka.consumer.properties.spring.deserializer.value.delegate.class=org.springframework.kafka.support.serializer.JsonDeserializer
   spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
   spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
   
   
   # JWT configuration
   jwt.token.secret=your-secret-token
   jwt.token.issuer=Dwi Didit Prasetiyo
   
   # 1 day in seconds
   jwt.token.accessValid=86400
   
   # 1 month (30 days) in seconds
   jwt.token.refreshValid=2592000
   ```

3. Build .jar using Maven
   ```bash
   mvn clean package
   ```
4. Build and run with Docker Compose
   ```bash
   docker-compose up --build
   ```

The application will run on http://localhost:9090

For API documentation on Swagger, visit here: http://localhost:9090/swagger-ui/index.html

To make requests with Postman, visit here: https://documenter.getpostman.com/view/32199524/2sA3XTezmK


# User Authentication and Authorization

On this RESTful API, there are three roles:

- **ROLE_ADMIN**
- **ROLE_USER**

Each role has specific permissions and access to different parts of the API. Authentication and authorization are handled using JWT (JSON Web Tokens).

## Authentication and Authorization Flow

### Login:
1. Users (Admin, Employer, Freelancer) log in using their credentials (username and password).
2. A JWT token is generated and returned to the user upon successful authentication.

### Accessing Protected Endpoints:
1. For every request to a protected endpoint, the JWT token must be included in the Authorization header as a Bearer token.
2. The server verifies the JWT token and extracts the user’s role.

### Role-Based Access Control:
1. The server checks if the user’s role has the necessary permissions to access the requested endpoint.
2. If the user has the required role, the request is processed.
3. If the user does not have the required role, an authorization error is returned.

## Database Initialization

When building and running the application, the database is automatically populated with dummy data using the Faker library. This includes creating initial users and products to help you get started quickly.

### Admin User
- **Username**: admin
- **Password**: admin

### Dummy Data
In addition to the initial users, the Faker library generates a variety of dummy data, including:

- **Users**: Additional users with randomly generated names, emails, and other details.
- **Products**: A range of products with randomly generated names, descriptions, categories, and prices.
- **Transactions**: Sample transactions to simulate user purchases and interactions with the platform.

This dummy data provides a rich dataset for testing and development purposes.

