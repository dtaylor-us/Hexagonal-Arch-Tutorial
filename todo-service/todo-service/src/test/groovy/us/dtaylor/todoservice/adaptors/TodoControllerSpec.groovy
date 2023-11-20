package us.dtaylor.todoservice.adaptors

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
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
}
