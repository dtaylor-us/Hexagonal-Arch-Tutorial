### Building the Second Microservice: User Management Service

#### Domain: Handling User Information and Their Todo Tasks

This microservice will focus on managing user information and their associations with todo tasks. Key functionalities include creating, retrieving, updating, and deleting user information, along with managing their todo tasks.

#### Service Setup

1. **Spring Boot Initialization**:
    - Create a new Spring Boot application specifically for user management.
    - Define the main application class, similar to the Todo service.
    - Example `UserServiceApplication.java`:
      ```java
      @SpringBootApplication
      public class UserServiceApplication {
          public static void main(String[] args) {
              SpringApplication.run(UserServiceApplication.class, args);
          }
      }
      ```

2. **WebFlux and Reactive MongoDB Configuration**:
    - Include dependencies for `spring-boot-starter-webflux` and `spring-boot-starter-data-mongodb-reactive` in your `pom.xml` or `build.gradle`.
    - Configure MongoDB in `application.properties`, possibly pointing to a different database or collection.

#### Implementing Hexagonal Architecture

1. **Defining Ports and Adapters**:
    - Similar to the Todo service, define primary and secondary ports and their corresponding adapters.

2. **Primary Port (User Service Interface)**:
    - Define the service interface for user-related operations.
    - Example `UserService.java`:
      ```java
      public interface UserService {
          Mono<User> createUser(User user);
          Flux<User> getAllUsers();
          Mono<User> getUserById(String id);
          Mono<User> updateUser(String id, User user);
          Mono<Void> deleteUser(String id);
      }
      ```

3. **Secondary Port (User Repository)**:
    - Define an interface for user data operations.
    - Example `UserRepository.java`:
      ```java
      public interface UserRepository extends ReactiveCrudRepository<User, String> {
          // Custom queries if needed
      }
      ```

4. **Domain Class for User**:
    - Create a `User` class representing user information.
    - Example `User.java`:
      ```java
      @Document
      public class User {
          @Id
          private String id;
          private String name;
          private String email;
          // Possibly reference to Todo IDs
 
          // Getters, Setters, Constructors
      }
      ```

#### Service Layer Implementation

1. **Implementing User Service**:
    - Implement the `UserService` interface.
    - Example `UserServiceImpl.java`:
      ```java
      @Service
      public class UserServiceImpl implements UserService {
          private final UserRepository userRepository;
 
          @Autowired
          public UserServiceImpl(UserRepository userRepository) {
              this.userRepository = userRepository;
          }
 
          @Override
          public Mono<User> createUser(User user) {
              return userRepository.save(user);
          }
 
          // Implement other methods
      }
      ```

2. **Primary Adapter (User Controller)**:
    - Implement a REST controller for user operations.
    - Example `UserController.java`:
      ```java
      @RestController
      @RequestMapping("/users")
      public class UserController {
          private final UserService userService;
 
          @Autowired
          public UserController(UserService userService) {
              this.userService = userService;
          }
 
          @PostMapping
          public Mono<ResponseEntity<User>> createUser(@RequestBody User user) {
              return userService.createUser(user)
                                .map(savedUser -> ResponseEntity.ok(savedUser));
          }
 
          // Implement other REST endpoints
      }
      ```

#### CRUD Operations with WebFlux and Reactive MongoDB

1. **Create a User**:
    - `POST /users`
    - Use the `createUser` method in `UserService`.

2. **Read Users**:
    - `GET /users` and `GET /users/{id}`
    - Implement `getAllUsers` and `getUserById` in `UserService`.

3. **Update a User**:
    - `PUT /users/{id}`
    - Implement `updateUser` in `UserService`.

4. **Delete a User**:
    - `DELETE /users/{id}`
    - Implement `deleteUser` in `UserService`.

5. **Associating Users with Todos**:
    - Extend the `User` class to include references to todo tasks.
    - Implement methods to associate, retrieve, and manage user-specific todo tasks.
