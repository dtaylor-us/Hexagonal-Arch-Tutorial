package us.dtaylor.todoservice.adapters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import us.dtaylor.todoservice.IntegrationTestConfiguration
import us.dtaylor.todoservice.domain.Todo
import us.dtaylor.todoservice.ports.TodoService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class TodoControllerSpec extends Specification {

    @Autowired
    WebTestClient webTestClient

    @Autowired
    TodoService todoService

    def 'getAll should return list of todos'() {
        given:
        def todo = new Todo(
                title: "Test",
                description: "Test",
                completed: false
        )
        todoService.getAllTodos() >> Flux.just(todo)

        when: "getAll endpoint is called"
        def response = webTestClient.get().uri("/api/v1/todos")
                .exchange()

        then: "the response is successful and contains the todos"
        response.expectStatus().isOk()
                .expectBodyList(Todo.class).hasSize(1)
                .consumeWith { result ->
                    assert result.responseBody[0].title == "Test"
                    assert result.responseBody[0].description == "Test"
                    assert !result.responseBody[0].completed
                }
    }

    // implement the rest of the tests
    def "create todo item"() {
        given:
        def todo = new Todo(
                title: "Test",
                description: "Test",
                completed: false
        )


        when: "create todo endpoint is called"
        todoService.createTodo(todo) >> Mono.just(todo)

        then: "the response is successful and contains the todo"
        StepVerifier.create(todoService.createTodo(todo))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()

    }

    def "get todo item by id"() {
        given: "a todo item"
        def todo = new Todo(
                title: "Test",
                description: "Test",
                completed: false
        )

        when: "get todo by id endpoint is called"
        todoService.getTodoById(todo.id) >> Mono.just(todo)

        then: "the response is successful and contains the todo"
        StepVerifier.create(todoService.getTodoById(todo.id))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

    //  - update todo item
    def "update todo item"() {
        given: "a todo item"
        def todo = new Todo(
                id: "1",
                title: "Test",
                description: "Test",
                completed: false
        )

        when: "update todo endpoint is called"
        todoService.updateTodo(todo.id, todo) >> Mono.just(todo)

        then: "the response is successful and contains the todo"
        StepVerifier.create(todoService.updateTodo(todo.id, todo))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

    def "delete todo item"() {
        given: "a todo item"
        def todo = new Todo(
                id: "1",
                title: "Test",
                description: "Test",
                completed: false
        )

        when: "delete todo endpoint is called"
        todoService.deleteTodo(todo.id) >> Mono.just(todo)

        then: "the response is successful and contains the todo"
        StepVerifier.create(todoService.deleteTodo(todo.id))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }
}
