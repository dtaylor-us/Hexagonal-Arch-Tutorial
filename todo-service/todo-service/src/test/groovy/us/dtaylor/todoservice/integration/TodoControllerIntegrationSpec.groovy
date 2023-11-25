package us.dtaylor.todoservice.integration

import org.slf4j.LoggerFactory
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
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
import spock.lang.Shared
import spock.lang.Specification
import us.dtaylor.todoservice.domain.Todo
import us.dtaylor.todoservice.domain.User
import us.dtaylor.todoservice.infastructure.persistence.TodoDocument

@EnableSharedInjection
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoControllerIntegrationSpec extends Specification {

    def static USER_ID
    static final UUID TODO_ID = UUID.randomUUID()
    public static final String DEFAULT_TITLE = "seed todo"

    @Autowired
    Environment environment

    @Shared
    @Autowired
    WebTestClient webTestClient

    @Shared
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
    static GenericContainer userServiceContainer = new GenericContainer(DockerImageName.parse("derektaylor/hexagonal-arch:user-service-0.0.2-SNAPSHOT"))
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

    def setupSpec() {
        def userServiceClient = createWebTestClient() // Automatically fetches the user service URL

        // set user id
        userServiceClient.get()
                .uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .consumeWith { result ->
                    USER_ID = result.responseBody.find({ it.name == "admin" }).id
                }

        // Seed the database
        Todo seed_todo = getTodo(DEFAULT_TITLE)
        seed_todo.setId(TODO_ID)
        Mono<Void> insertMono = reactiveMongoTemplate.insert(TodoDocument.toDocument(seed_todo)).then()

        StepVerifier.create(insertMono)
                .expectComplete()
                .verify()
    }

    private WebTestClient createWebTestClient() {
        String userServiceUrl = environment.getProperty("user.service.url")
        return WebTestClient.bindToServer()
                .baseUrl(userServiceUrl)
                .build()
    }

    def getTodo(String title) {
        return new Todo(
                title: title,
                description: "Description for a test comment: $title",
                completed: false,
                userId: USER_ID
        )
    }

    def insertTodo(String title) {
        def todo = getTodo(title)
        def id = UUID.randomUUID()
        todo.setId(id)

        Mono<Void> insertMono = reactiveMongoTemplate.insert(TodoDocument.toDocument(todo)).then()

        StepVerifier.create(insertMono)
                .expectComplete()
                .verify()
        return [id, todo]
    }

    def 'getAll should return list of todos'() {
        given:
        Todo expectedTodo = getTodo(DEFAULT_TITLE)

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
        Todo todo1 = new Todo(
                title: "created todo",
                description: "crud test",
                completed: false,
                userId: USER_ID
        )

        when: "createTodo endpoint is called"
        def response = webTestClient.post().uri("/api/v1/todos")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(todo1)
                .exchange()

        then: "the response is successful and contains the created todo"
        response.expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith { result ->
                    assert result.responseBody.title == todo1.title
                    assert result.responseBody.description == todo1.description
                    assert !result.responseBody.completed
                }
    }

    def 'getTodoById should return todo'() {
        when: "getTodoById endpoint is called"
        def response = webTestClient.get().uri("/api/v1/todos/{id}", TODO_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the todo"
        response.expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith { result ->
                    assert result.responseBody.title == DEFAULT_TITLE
                    assert !result.responseBody.completed
                }
    }

    def 'getAllTodosByUserId should return list of todos'() {
        given: "a user todo"
        def (UUID id, Todo todo) = insertTodo("user todo")

        when: "getAllTodosByUserId endpoint is called"
        def response = webTestClient.get().uri("/api/v1/todos/user/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the todos"
        response.expectStatus().isOk()
                .expectBodyList(Todo.class)

                .contains(todo)
                .consumeWith { result ->
                    assert result.responseBody.find { it ->
                        it.id == id
                    }
                }

    }

    def 'updateTodo should return updated todo'() {
        given:
        Todo updated_todo = getTodo("updated todo")

        when: "updateTodo endpoint is called"
        def response = webTestClient.put().uri("/api/v1/todos/{id}", TODO_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(updated_todo)
                .exchange()

        then: "the response is successful and contains the updated todo"
        response.expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith { result ->
                    assert result.responseBody.title == updated_todo.title
                    assert result.responseBody.description == updated_todo.description
                    assert !result.responseBody.completed
                }
    }

    def 'deleteById should return no content'() {
        setup: "a user todo"
        def (UUID id, Todo _) = insertTodo("delete todo")

        when: "deleteById endpoint is called"
        def response = webTestClient.delete().uri("/api/v1/todos/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the updated todo"
        response.expectStatus().isOk()
    }
}
