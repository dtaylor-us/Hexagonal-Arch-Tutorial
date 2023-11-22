package us.dtaylor.todoservice.adapters

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import us.dtaylor.todoservice.domain.Todo

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerSpec extends Specification {
    static final String USER_ID = "1"

    @Autowired
    WebTestClient webTestClient

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate

    // Shared network
    static Network network = Network.newNetwork()

    // MongoDB container
    static GenericContainer mongoDBContainer = new GenericContainer<>(DockerImageName.parse("mongo:4.0.10"))
            .withExposedPorts(27017)
            .withNetwork(network)
            .withNetworkAliases("mongodb")
            .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
            .withEnv("MONGO_INITDB_ROOT_PASSWORD", "dasspassword");

    // User Service container
    static GenericContainer userServiceContainer = new GenericContainer(DockerImageName.parse("hexogonal_arch_tutorial_user-service:latest"))
            .withNetwork(network)
            .withNetworkAliases("user-service")
            .withExposedPorts(8085)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("user-service")))
            .withEnv("SPRING_PROFILES_ACTIVE", "db-seed")
            .withEnv("SPRING_PROFILES_ACTIVE", "db-seed")
            .withEnv("SPRING_DATA_MONGODB_URI", "mongodb://admin:dasspassword@mongodb:27017")
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200));

    static {
        mongoDBContainer.start();
        userServiceContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        String mongoDbUri = String.format("mongodb://admin:dasspassword@%s:%s",
                mongoDBContainer.getHost(), mongoDBContainer.getMappedPort(27017));
        registry.add("spring.data.mongodb.uri", () -> mongoDbUri);

        String userServiceUrl = String.format("http://%s:%s",
                userServiceContainer.getHost(), userServiceContainer.getMappedPort(8085));
        registry.add("user.service.url", () -> userServiceUrl);
    }

    def setup() {
        // Seed the database
        Todo todo = getTodo()
        Mono<Void> insertMono = reactiveMongoTemplate.insert(todo).then()

        StepVerifier.create(insertMono)
                .expectComplete()
                .verify()
    }

    def getTodo() {
        return Todo.builder()
                .title("Test")
                .description("Test")
                .completed(false)
                .userId(USER_ID)
                .build()
    }

    def 'getAll should return list of todos'() {
        given:
        Todo expectedTodo = getTodo()

        when: "getAll endpoint is called"
        def response = webTestClient.get().uri("/api/v1/todos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the todos"
        response.expectStatus().isOk()
                .expectBodyList(Todo.class)
                .consumeWith { result ->
                    assert result.responseBody.any { todo ->
                        todo.title == expectedTodo.title &&
                                todo.description == expectedTodo.description &&
                                todo.completed == expectedTodo.completed
                    }
                }
    }

    def 'createTodo should return created todo'() {
        given:
        Todo todo = Todo.builder()
                .title("created todo")
                .description("crud test")
                .completed(false)
                .userId(USER_ID)
                .build()

        when: "createTodo endpoint is called"
        def response = webTestClient.post().uri("/api/v1/todos/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(todo)
                .exchange()

        then: "the response is successful and contains the created todo"
        response.expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith { result ->
                    assert result.responseBody.title == todo.title
                    assert result.responseBody.description == todo.description
                    assert !result.responseBody.completed
                }
    }
//
//    def 'getTodoById should return todo'() {
//        given:
//        Todo todo = getTodo()
//        todoService.getTodoById(todo.id) >> Mono.just(todo)
//
//        when: "getTodoById endpoint is called"
//        def response = webTestClient.get().uri("/api/v1/todos/{id}", todo.id)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//
//        then: "the response is successful and contains the todo"
//        response.expectStatus().isOk()
//                .expectBody(Todo.class)
//                .consumeWith { result ->
//                    assert result.responseBody.title == todo.title
//                    assert result.responseBody.description == todo.description
//                    assert !result.responseBody.completed
//                }
//    }
//
//    def 'getAllTodosByUserId should return list of todos'() {
//        given:
//        Todo todo = getTodo()
//        todoService.getAllTodosByUserId(USER_ID) >> Flux.just(todo)
//
//        when: "getAllTodosByUserId endpoint is called"
//        def response = webTestClient.get().uri("/api/v1/todos/user/{userId}", USER_ID)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//
//        then: "the response is successful and contains the todos"
//        response.expectStatus().isOk()
//                .expectBodyList(Todo.class)
//                .hasSize(1)
//                .consumeWith { result ->
//                    assert result.responseBody[0].title == todo.title
//                    assert result.responseBody[0].description == todo.description
//                    assert !result.responseBody[0].completed
//                }
//    }
//
//    def 'updateTodo should return updated todo'() {
//        given:
//        Todo todo = getTodo()
//        todoService.updateTodo(todo.id, todo) >> Mono.just(todo)
//
//        when: "updateTodo endpoint is called"
//        def response = webTestClient.put().uri("/api/v1/todos/{id}", todo.id)
//                .accept(MediaType.APPLICATION_JSON)
//                .bodyValue(todo)
//                .exchange()
//
//        then: "the response is successful and contains the updated todo"
//        response.expectStatus().isOk()
//                .expectBody(Todo.class)
//                .consumeWith { result ->
//                    assert result.responseBody.title == todo.title
//                    assert result.responseBody.description == todo.description
//                    assert !result.responseBody.completed
//                }
//    }
//
//    def 'deleteById should return no content'() {
//        given:
//        Todo todo = getTodo()
//        todoService.deleteTodo(todo.id) >> Mono.empty()
//
//        when: "deleteById endpoint is called"
//        def response = webTestClient.delete().uri("/api/v1/todos/{id}", todo.id)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//
//        then: "the response is successful and contains the updated todo"
//        response.expectStatus().isOk()
//    }
}
