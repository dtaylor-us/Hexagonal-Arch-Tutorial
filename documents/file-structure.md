# Tutorial: Structuring a Spring Boot Application with Hexagonal Architecture

## Introduction
Hexagonal Architecture, also known as Ports and Adapters pattern, is a design approach that promotes the separation of concerns in an application. It divides an application into an inside part (business logic) and an outside part (interactions with the external world).

## Key Components
1. **Domain**: Contains the core business logic.
2. **Ports**: Interfaces that define how the application communicates with the outside world.
    - **Primary Ports**: Used by the outside world to interact with the application (e.g., service interfaces).
    - **Secondary Ports**: Used by the application to interact with external systems (e.g., data repositories).
3. **Adapters**: Implementations of the ports.
    - **Primary Adapters**: Adapt external requests into calls to the primary ports (e.g., REST Controllers).
    - **Secondary Adapters**: Implement secondary ports to interact with external resources (e.g., database access).

## Example Directory Structure for a Todo Service

```
todo-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── us/
│   │   │   │   ├── dtaylor/
│   │   │   │   │   ├── todoservice/
│   │   │   │   │   │   ├── application/      # Application layer
│   │   │   │   │   │   │   ├── ports/        # Primary ports (interfaces)
│   │   │   │   │   │   │   │   └── TodoService.java
│   │   │   │   │   │   │   ├── config/       # Configuration classes
│   │   │   │   │   │   │   └── dto/          # Data Transfer Objects
│   │   │   │   │   │   ├── domain/           # Domain layer (core business logic)
│   │   │   │   │   │   │   ├── Todo.java
│   │   │   │   │   │   │   └── User.java
│   │   │   │   │   │   ├── infrastructure/   # Infrastructure layer
│   │   │   │   │   │   │   ├── adapters/     # Secondary adapters
│   │   │   │   │   │   │   │   ├── persistence/ # Adapter for database access
│   │   │   │   │   │   │   │   │   └── TodoRepository.java
│   │   │   │   │   │   │   │   └── rest/     # Primary adapters (controllers)
│   │   │   │   │   │   │   │       └── TodoController.java
│   │   │   │   │   │   │   └── config/       # Infrastructure specific configurations
│   │   │   ├── TodoServiceApplication.java   # Spring Boot Application Entry Point
│   │   └── resources/
│   │       ├── application.properties
│   └── test/
│       ├── java/
│       │   └── us/
│       │       └── dtaylor/
│       │           └── todoservice/
│       │               ├── application/
│       │               ├── domain/
│       │               └── infrastructure/
└── pom.xml or build.gradle
```

## Example Code

1. **Domain (Core Business Logic)**:
   ```java
   // File: src/main/java/us/dtaylor/todoservice/domain/Todo.java
   package us.dtaylor.todoservice.domain;

   public class Todo {
       private String id;
       private String title;
       // ... other fields, constructors, getters, setters ...
   }
   ```

2. **Primary Port (Service Interface)**:
   ```java
   // File: src/main/java/us/dtaylor/todoservice/application/ports/TodoService.java
   package us.dtaylor.todoservice.application.ports;

   import us.dtaylor.todoservice.domain.Todo;
   import java.util.List;

   public interface TodoService {
       Todo createTodo(Todo todo);
       List<Todo> getAllTodos();
       // ... other methods ...
   }
   ```

3. **Primary Adapter (REST Controller)**:
   ```java
   // File: src/main/java/us/dtaylor/todoservice/infrastructure/adapters/rest/TodoController.java
   package us.dtaylor.todoservice.infrastructure.adapters.rest;

   import org.springframework.web.bind.annotation.RestController;
   import org.springframework.web.bind.annotation.RequestMapping;
   import us.dtaylor.todoservice.application.ports.TodoService;
   // ... imports ...

   @RestController
   @RequestMapping("/api/todos")
   public class TodoController {
       private final TodoService todoService;
       
       // Constructor, request mappings for CRUD operations
