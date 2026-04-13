TaskFlow — Task Management System
1. Overview
    

    TaskFlow is a backend-heavy task and project management system. It  It provides:

    User authentication (JWT-based)
    Project management (CRUD)
    Task management within projects
    Task assignment and filtering
    Role/ownership-based access control (ACL-style logic)
    Database versioning using Flyway
    Fully containerized setup using Docker Compose
    
    Tech Stack:
    Backend
    Java 21
    Spring Boot (Web, Security, Data JPA)
    PostgreSQL
    Flyway (DB migrations)
    JWT (authentication)
    BCrypt (password hashing)
    Infrastructure
    Docker
    Docker Compose

2. Architecture Decisions (Layered Architecture)


    The system follows a strict layered architecture:
    
    Controller → Service → Repository → Database
    
    Why this structure?
    Separation of concerns: business logic is isolated in services
    Testability: services can be unit-tested independently
    Scalability: easy to extend modules (tasks, projects, users)
    Security Design (JWT)
    Stateless authentication using JWT
    Spring Security filter chain validates tokens
    Public endpoints:
    /auth/register
    /auth/login
    
    All other endpoints require authentication.
    
    
    Database Design
    
    Key entities:
    
    USERS
    PROJECTS (owned by users)
    TASKS (belong to projects + assigned to users)
    
    Relationships:
    User → Projects (1:N)
    Project → Tasks (1:N)
    User → Tasks (assigned_to relationship)

    Tradeoffs Made
    1. No microservices
       Monolith chosen intentionally for simplicity and deployability
       2. No event-driven architecture
          Kept synchronous REST flow for clarity and debugging ease
       3. Running Locally (Docker Only)


3. Running Locally
    

    Requires only Docker + Docker Compose installed
    
    git clone: https://github.com/Shashank2104125/taskflow
    
    cd taskflow
    
    cp .env.example .env
    
    docker compose up --build
    
    Access:
    Backend API: http://localhost:8081
    PostgreSQL: localhost:5432


4. Running Migrations


    Flyway runs automatically on startup.
    
    Migrations are located at:
    
    src/main/resources/db/migration
    If manual reset is needed:
    docker compose down -v
    docker compose up --build


5. Test Credentials


    Seed/Test user available after startup:

    Email: test@example.com &&
    Password: password123


6. API Reference


    https://drive.google.com/file/d/1AHzxGlc6zPNw6gp77JK2iPct73FptLPT/view?usp=sharing



7. Future Implementation


    Rate limiting
    API gateway
    Kubernetes deployment readiness

    My Shortcuts:
    
    1. I use Claude to design HLD, LLD, and entity-relationship diagrams.
    2. I use Cursor to write code faster.
    3. I use ChatGPT for ad-hoc task understanding and logic validation.
    

