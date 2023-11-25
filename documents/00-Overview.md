# Spring Boot Hexagonal Architecture Tutorial

## Tutorial Overview

1. Introduction to Hexagonal Architecture
    * Explanation of hexagonal architecture principles
    * Benefits for microservices
2. Setting Up the Project Environment
    * Installing necessary tools (Java, Spring Boot, MongoDB, etc.)
    * Creating a Spring Boot project
3. Building the First Microservice: Todo Management Service
   * Domain: Managing todo tasks
   * Service Setup:
     * Spring Boot initialization
     * WebFlux configuration
     * Reactive MongoDB integration
   * Implementing Hexagonal Architecture:
     * Defining Ports and Adapters
     * Service layer implementation
   * Code examples for CRUD operations with WebFlux and Reactive MongoDB
 4. Building the Second Microservice: User Management Service
    * Domain: Handling user information and their todo tasks
    * Similar setup and architecture as the first service
 5. Inter-Service Communication
    * Techniques for microservices communication (REST, message brokers, etc.)
    * Implementing a client in Service 1 to communicate with Service 2
6. Testing
   * Writing unit and integration tests
   * Tools and frameworks for testing reactive applications
7. Deployment and Monitoring
   * Containerization with Docker
   * Basic monitoring setup

## Sample Code and Configuration

### 1. Project Initialization
```xml
<!-- pom.xml dependencies for WebFlux and Reactive MongoDB -->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>
    <!-- Other dependencies -->
</dependencies>

```
### 2. Hexagonal Architecture Components

* Ports: Interfaces representing the primary and secondary ports
* Adapters: Implementations of the ports
* Domain: Core business logic
```java
// Example of a port
public interface TodoRepository {
    Mono<Todo> save(Todo todo);
    Flux<Todo> findAll();
    Mono<Todo> findById(String id);
    Mono<Void> deleteById(String id);
}
```
```java
// Adapter using Reactive MongoDB
@Repository
public class ReactiveMongoTodoRepository implements TodoRepository {
    // Implementation using ReactiveMongoTemplate or ReactiveCrudRepository
}
```

### 3. Service Layer Implementation
* Implementing CRUD operations
* Reactive programming patterns
```java
@Service
public class TodoService {
    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public Mono<Todo> createTodo(Todo todo) {
        return repository.save(todo);
    }
    // Other CRUD methods
}
```
### 4 REST Controller Using WebFlux
```java 
@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Todo>> createTodo(@RequestBody Todo todo) {
        return service.createTodo(todo)
                      .map(savedTodo -> ResponseEntity.ok(savedTodo));
    }

    // Other endpoints for CRUD operations
}
```

### 5 Inter-Service Communication Example
* Using WebClient for reactive HTTP requests
```java
@Service
public class UserTodoService {
    private final WebClient webClient;

    public UserTodoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://todo-service").build();
    }

    public Flux<Todo> getUserTodos(String userId) {
        return webClient.get()
                        .uri("/todos/user/{userId}", userId)
                        .retrieve()
                        .bodyToFlux(Todo.class);
    }
}
```
### 6. Dockerization 
* Dockerfile for Spring Boot application
```dockerfile
# Dockerfile
FROM openjdk:11
COPY target/microservice.jar microservice.jar
ENTRYPOINT ["java", "-jar", "/microservice.jar"]
```
