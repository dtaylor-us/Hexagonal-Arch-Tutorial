### Building the First Microservice: Todo Management Service

#### Domain: Managing Todo Tasks

This microservice will focus on the management of todo tasks. The basic functionalities include creating, reading, updating, and deleting (CRUD) todo tasks.

#### Service Setup

1. **Spring Boot Initialization**:
    - Create a Spring Boot application (as outlined in the previous section).
    - Define the main application class.
    - Example `TodoServiceApplication.java`:
      ```java
      @SpringBootApplication
      public class TodoServiceApplication {
 
          public static void main(String[] args) {
              SpringApplication.run(TodoServiceApplication.class, args);
          }
      }
      ```

2. **WebFlux Configuration**:
    - By including `spring-boot-starter-webflux` in your dependencies, Spring Boot auto-configures WebFlux.
    - You can customize configurations in `application.properties` or `application.yml`.

3. **Reactive MongoDB Integration**:
    - Include `spring-boot-starter-data-mongodb-reactive` in your dependencies.
    - Configure MongoDB properties in `application.properties`.
    - Example:
      ```properties
      spring.data.mongodb.uri=mongodb://localhost:27017/todoDB
      ```

#### Implementing Hexagonal Architecture

1. **Defining Ports and Adapters**:
    - **Ports**: Interfaces for primary and secondary interactions.
    - **Adapters**: Implementations of these interfaces.

2. **Primary Port (Service Interface)**:
    - Define an interface for the core business logic.
    - Example `TodoService.java`:
      ```java
      public interface TodoService {
          Mono<Todo> createTodo(Todo todo);
          Flux<Todo> getAllTodos();
          Mono<Todo> getTodoById(String id);
          Mono<Todo> updateTodo(String id, Todo todo);
          Mono<Void> deleteTodo(String id);
      }
      ```

3. **Secondary Port (Persistence Interface)**:
    - Define an interface for data operations.
    - Example `TodoRepository.java`:
      ```java
      public interface TodoRepository extends ReactiveCrudRepository<Todo, String> {
          // Custom queries if needed
      }
      ```

4. **Domain Class**:
    - Define a domain class representing a todo task.
    - Example `Todo.java`:
      ```java
      @Document
      public class Todo {
          @Id
          private String id;
          private String title;
          private String description;
          private boolean completed;
 
          // Getters, Setters, Constructors
      }
      ```

#### Service Layer Implementation

1. **Implementing the Service**:
    - Implement the primary port using the service class.
    - Example `TodoServiceImpl.java`:
      ```java
      @Service
      public class TodoServiceImpl implements TodoService {
          private final TodoRepository repository;
 
          @Autowired
          public TodoServiceImpl(TodoRepository repository) {
              this.repository = repository;
          }
 
          @Override
          public Mono<Todo> createTodo(Todo todo) {
              return repository.save(todo);
          }
 
          // Implement other methods
      }
      ```

2. **Primary Adapter (REST Controller)**:
    - Implement a REST controller to expose the functionalities.
    - Example `TodoController.java`:
      ```java
      @RestController
      @RequestMapping("/todos")
      public class TodoController {
          private final TodoService todoService;
 
          @Autowired
          public TodoController(TodoService todoService) {
              this.todoService = todoService;
          }
 
          @PostMapping
          public Mono<ResponseEntity<Todo>> createTodo(@RequestBody Todo todo) {
              return todoService.createTodo(todo)
                                .map(savedTodo -> ResponseEntity.ok(savedTodo));
          }
 
          // Implement other REST endpoints
      }
      ```

#### CRUD Operations with WebFlux and Reactive MongoDB

1. **Create a Todo**:
    - `POST /todos`
    - Use the `createTodo` method in `TodoService`.

2. **Read Todos**:
    - `GET /todos` and `GET /todos/{id}`
    - Implement `getAllTodos` and `getTodoById` in `TodoService`.

3. **Update a Todo**:
    - `PUT /todos/{id}`
    - Implement `updateTodo` in `TodoService`.

4. **Delete a Todo**:
    - `DELETE /todos/{id}`
    - Implement `deleteTodo` in `TodoService`.

5. **Reactive Data Access with Repository**:
    - The `TodoRepository` extends `ReactiveCrudRepository`, providing reactive CRUD operations.

6. **Exception Handling**:
    - Implement global exception handling using `@ControllerAdvice` to handle scenarios like not found, invalid inputs, etc.
