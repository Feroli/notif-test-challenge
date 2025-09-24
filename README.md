# Notification System - Gila Software Test Challenge

## Overview

A Spring Boot notification system implementing hexagonal architecture. The system receives messages categorized by topic and forwards them to subscribed users through their preferred notification channels (SMS, Email, Push Notification).

## Features

- **Message Categories**: Sports, Finance, Movies
- **Notification Channels**: SMS, Email, Push Notification
- **User Subscription Management**: Users subscribe to specific categories and choose notification channels
- **Notification Logging**: Complete audit trail of all notifications sent
- **Web Interface**: Form for sending messages and real-time log display
- **Fault Tolerance**: Graceful handling of notification failures with retry logic

## Architecture

### Hexagonal Architecture (Ports & Adapters)
```
src/main/java/com/gila/notification/
├── domain/                 # Core business logic
│   ├── model/              # Domain entities
│   ├── port/               # Interfaces (ports)
│   │   ├── in/             # Inbound ports (use cases)
│   │   └── out/            # Outbound ports (repositories, services)
│   └── service/            # Domain services
├── application/            # Application layer
│   ├── service/            # Application services
│   ├── dto/                # Data Transfer Objects
│   └── mapper/             # Object mappers
└── infrastructure/         # Infrastructure layer
    ├── adapter/
    │   ├── in/web/         # REST controllers
    │   └── out/            # External implementations
    │       ├── persistence/ # Database adapters
    │       └── notification/ # Notification senders
    ├── config/             # Configuration
    └── exception/          # Exception handling
```

### Design Patterns Used

1. **Strategy Pattern**: For selecting notification channels dynamically
2. **Repository Pattern**: For data access abstraction
3. **Factory Pattern**: For creating notification senders
4. **Builder Pattern**: For constructing domain objects
5. **Dependency Injection**: Throughout the application

### SOLID Principles Applied

- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Extensible for new notification channels without modifying existing code
- **Liskov Substitution**: All notification senders implement the same interface
- **Interface Segregation**: Small, focused interfaces for specific responsibilities
- **Dependency Inversion**: Depends on abstractions, not concretions

## Tech Stack

### Backend
- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **H2 Database** (In-memory for development)
- **MySQL** (Production ready)
- **Lombok**
- **JUnit 5** & **Mockito** (Testing)
- **Gradle** (Build tool)
- **Flyway** (Database migrations)

### Frontend
- **React 19** (JavaScript)
- **Pure CSS** (No frameworks)
- **Fetch API** (HTTP requests)

## Getting Started

### Prerequisites
- Java 21
- Node.js 18+ and npm
- Gradle 8.x (included via wrapper)

### Running the Application

#### Backend
```bash
# Clone the repository
git clone https://github.com/yourusername/notif-test-challenge.git
cd notif-test-challenge

# Build the project
./gradlew build

# Run the Spring Boot backend
./gradlew bootRun
```

The backend API will start on `http://localhost:8080`

#### Frontend
```bash
# In a new terminal, navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the React development server
npm start
```

The frontend will start on `http://localhost:3000`

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport
```

## API Documentation

### Send Message
```http
POST /api/notifications/send
Content-Type: application/json

{
  "category": "SPORTS",
  "message": "Your message content here"
}
```

**Response:**
```json
{
  "messageId": 1,
  "status": "SUCCESS",
  "totalUsersNotified": 5,
  "successfulNotifications": 8,
  "failedNotifications": 2,
  "message": "Message sent successfully. 8 successful, 2 failed notifications."
}
```

### Get Notification Logs
```http
GET /api/notifications/logs
```

### Get Logs by User
```http
GET /api/notifications/logs/user/{userId}
```

### Get Logs by Message
```http
GET /api/notifications/logs/message/{messageId}
```

### Get Categories
```http
GET /api/notifications/categories
```

## Web Interface

Access the web interface at `http://localhost:3000`

Features:
- **Simple submission form** with category dropdown and message textarea
- **Notification log table** displaying all sent notifications
- **Real-time updates** showing user, channel, and status for each notification
- **Clean, minimalist design** focused on functionality

## Mock Users

The system comes pre-populated with 10 mock users:

| ID | Name | Categories | Channels |
|----|------|------------|----------|
| 1 | John Doe | Sports, Finance | SMS, Email |
| 2 | Jane Smith | Movies | Email |
| 3 | Bob Johnson | Sports, Movies | SMS, Push |
| 4 | Alice Brown | Finance | Push |
| 5 | Charlie Wilson | All Categories | All Channels |
| 6 | Diana Martinez | Movies, Finance | Email, Push |
| 7 | Edward Davis | Sports | SMS |
| 8 | Fiona Garcia | Finance, Movies | Email (No phone) |
| 9 | George Lee | Sports | SMS, Push (No email) |
| 10 | Helen White | None | Email |

## Database Access

H2 Console is available at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:notificationdb`
- Username: `sa`
- Password: (empty)

## Configuration

Application properties can be modified in `src/main/resources/application.properties`

Key configurations:
- `server.port`: Application port (default: 8080)
- `spring.datasource.url`: Database connection URL
- `spring.jpa.hibernate.ddl-auto`: Database schema generation strategy

## Testing Strategy

The project follows Test-Driven Development (TDD) with comprehensive test coverage:

- **Unit Tests**: For domain models, services, and strategies
- **Integration Tests**: For repository and controller layers
- **Mock Tests**: Using Mockito for isolated testing
- **Test Coverage**: All critical business logic is covered

## Fault Tolerance Features

- **Retry Mechanism**: Automatic retry with exponential backoff for transient failures
- **Circuit Breaker Pattern**: Prevents cascading failures by temporarily blocking requests after threshold failures
- **Graceful Error Handling**: All failures are logged with descriptive error messages
- **10% Simulated Failure Rate**: For demonstration purposes, the system randomly simulates failures
