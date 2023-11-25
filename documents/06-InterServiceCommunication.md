### Inter-Service Communication

Inter-service communication is crucial in a microservices architecture, allowing different services to interact and collaborate. Common techniques include RESTful APIs, message brokers, gRPC, and others. For simplicity, we'll focus on RESTful communication and implementing a client in Service 1 (Todo Management Service) to communicate with Service 2 (User Management Service).

#### Techniques for Microservices Communication

1. **RESTful APIs**:
    - Services expose RESTful endpoints that can be consumed by other services.
    - Easy to use and understand, but synchronous and can lead to tight coupling if not designed carefully.

2. **Message Brokers** (e.g., RabbitMQ, Kafka):
    - Enables asynchronous communication.
    - Useful for event-driven architectures, decoupling services, and handling peak loads.

3. **gRPC**:
    - A high-performance RPC framework.
    - Useful for efficient communication, especially in polyglot environments.

#### Implementing a Client in Service 1 to Communicate with Service 2

We will implement a client in the Todo Management Service to consume endpoints exposed by the User Management Service.

##### Step 1: Add Dependencies for Web Client

In the `pom.xml` or `build.gradle` of the Todo Management Service, add a dependency for Spring WebFlux, which includes the WebClient:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

##### Step 2: Create a User Client Class

We'll create a `UserClient` class in the Todo Management Service to interact with the User Management Service:

```java
@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Value("${user.service.url}") String userServiceUrl) {
        this.webClient = WebClient.builder()
                                  .baseUrl(userServiceUrl)
                                  .build();
    }

    public Mono<User> getUserById(String userId) {
        return webClient.get()
                        .uri("/users/{id}", userId)
                        .retrieve()
                        .bodyToMono(User.class);
    }

    // Other methods like updateUser, deleteUser, etc.
}
```

Here, `userServiceUrl` should be configured in the `application.properties` file of the Todo Management Service, pointing to the URL where the User Management Service is running.

##### Step 3: Integrate User Client in Todo Service

Modify the Todo service to include operations that require user information. For instance, before adding a todo, you might want to ensure the user exists:

```java
@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserClient userClient;

    @Autowired
    public TodoServiceImpl(TodoRepository todoRepository, UserClient userClient) {
        this.todoRepository = todoRepository;
        this.userClient = userClient;
    }

    @Override
    public Mono<Todo> createTodoForUser(String userId, Todo todo) {
        return userClient.getUserById(userId)
                         .flatMap(user -> todoRepository.save(todo));
    }

    // Other methods
}
```

In this example, `createTodoForUser` first checks if the user exists by calling `getUserById` from `UserClient`. If the user exists, it proceeds to save the todo.

##### Step 4: Handling Errors and Timeouts

When dealing with inter-service communication, it's crucial to handle errors and timeouts gracefully:

```java
public Mono<User> getUserById(String userId) {
    return webClient.get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> 
                        Mono.error(new RuntimeException("User not found")))
                    .bodyToMono(User.class)
                    .timeout(Duration.ofSeconds(3))
                    .onErrorResume(e -> {
                        // Log error, return a fallback or error signal
                        return Mono.error(new RuntimeException("Service unavailable"));
                    });
}
```
