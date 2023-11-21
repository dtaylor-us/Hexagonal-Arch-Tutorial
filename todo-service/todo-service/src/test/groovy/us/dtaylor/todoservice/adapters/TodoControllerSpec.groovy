package us.dtaylor.todoservice.adapters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Specification
import us.dtaylor.todoservice.IntegrationTestConfiguration
import us.dtaylor.todoservice.domain.Todo
import us.dtaylor.todoservice.ports.TodoService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TodoControllerSpec extends Specification {
    static final String USER_ID = "2"
    @Autowired
    WebTestClient webTestClient

    @Autowired
    TodoService todoService

    def getTodo() {
        return Todo.builder()
                .id("1")
                .title("Test")
                .description("Test")
                .completed(false)
                .userId(USER_ID)
                .build()
    }


    def 'getAll should return list of todos'() {
        given:
        Todo todo = getTodo()
        todoService.getAllTodos() >> Flux.just(todo)

        when: "getAll endpoint is called"
        def response = webTestClient.get().uri("/api/v1/todos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the todos"
        response.expectStatus().isOk()
                .expectBodyList(Todo.class)
                .hasSize(1)
                .consumeWith { result ->
                    assert result.responseBody[0].title == todo.title
                    assert result.responseBody[0].description == todo.description
                    assert !result.responseBody[0].completed
                }
    }

    def 'createTodo should return created todo'() {
        given:
        Todo todo = getTodo()
        todoService.createTodo(USER_ID, todo) >> Mono.just(todo)

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

    def 'getTodoById should return todo'() {
        given:
        Todo todo = getTodo()
        todoService.getTodoById(todo.id) >> Mono.just(todo)

        when: "getTodoById endpoint is called"
        def response = webTestClient.get().uri("/api/v1/todos/{id}", todo.id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the todo"
        response.expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith { result ->
                    assert result.responseBody.title == todo.title
                    assert result.responseBody.description == todo.description
                    assert !result.responseBody.completed
                }
    }

    def 'getAllTodosByUserId should return list of todos'() {
        given:
        Todo todo = getTodo()
        todoService.getAllTodosByUserId(USER_ID) >> Flux.just(todo)

        when: "getAllTodosByUserId endpoint is called"
        def response = webTestClient.get().uri("/api/v1/todos/user/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the todos"
        response.expectStatus().isOk()
                .expectBodyList(Todo.class)
                .hasSize(1)
                .consumeWith { result ->
                    assert result.responseBody[0].title == todo.title
                    assert result.responseBody[0].description == todo.description
                    assert !result.responseBody[0].completed
                }
    }

    def 'updateTodo should return updated todo'() {
        given:
        Todo todo = getTodo()
        todoService.updateTodo(todo.id, todo) >> Mono.just(todo)

        when: "updateTodo endpoint is called"
        def response = webTestClient.put().uri("/api/v1/todos/{id}", todo.id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(todo)
                .exchange()

        then: "the response is successful and contains the updated todo"
        response.expectStatus().isOk()
                .expectBody(Todo.class)
                .consumeWith { result ->
                    assert result.responseBody.title == todo.title
                    assert result.responseBody.description == todo.description
                    assert !result.responseBody.completed
                }
    }

    def 'deleteById should return no content'() {
        given:
        Todo todo = getTodo()
        todoService.deleteTodo(todo.id) >> Mono.empty()

        when: "deleteById endpoint is called"
        def response = webTestClient.delete().uri("/api/v1/todos/{id}", todo.id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

        then: "the response is successful and contains the updated todo"
        response.expectStatus().isOk()
    }
}
