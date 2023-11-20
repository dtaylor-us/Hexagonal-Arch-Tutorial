package us.dtaylor.todoservice.ports

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.crossstore.ChangeSetPersister
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject
import us.dtaylor.todoservice.domain.Todo

@SpringBootTest
class TodoServiceImplSpec extends Specification {

    @Subject
    TodoServiceImpl todoService

    TodoRepository todoRepository = Mock()

    def setup() {
        todoService = new TodoServiceImpl(todoRepository)
    }

    def "create todo item"() {
        given:
        def todo = new Todo(
                title: "Test",
                description: "Test",
                completed: false
        )

        when:
        todoRepository.save(todo) >> Mono.just(todo)

        then:
        StepVerifier.create(todoService.createTodo(todo))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()

    }

    def "get all todo items"() {
        given:
        def todo = new Todo(
                title: "Test",
                description: "Test",
                completed: false
        )

        when:
        todoRepository.findAll() >> Flux.just(todo)

        then:
        StepVerifier.create(todoService.getAllTodos())
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

    def "get todo item by id"() {
        given:
        def todo = new Todo(
                title: "Test",
                description: "Test",
                completed: false
        )

        when:
        todoRepository.findById(todo.id) >> Mono.just(todo)

        then:
        StepVerifier.create(todoService.getTodoById(todo.id))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

    def "update todo item"() {
        given:
        def todo = new Todo(
                id: "1",
                title: "Test",
                description: "Test",
                completed: false
        )

        when:
        todoRepository.findById(todo.id) >> Mono.just(todo)
        todoRepository.save(todo) >> Mono.just(todo)

        then:
        StepVerifier.create(todoService.updateTodo(todo.id, todo))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

    def "delete todo item - success"() {
        given:
        def todo = new Todo(
                id: "1",
                title: "Test",
                description: "Test",
                completed: false
        )
        todoRepository.findById(todo.id) >> Mono.just(todo)
        todoRepository.delete(todo) >> Mono.just(Void)

        when:
        Mono<Void> result = todoService.deleteTodo(todo.id)

        then:
        StepVerifier.create(result)
                .expectNext(Void as Void)
                .verifyComplete()
    }

    def "delete todo item - not found"() {
        given:
        String nonExistentId = "2"
        todoRepository.findById(nonExistentId) >> Mono.empty()

        when:
        Mono<Void> result = todoService.deleteTodo(nonExistentId)

        then:
        StepVerifier.create(result)
                .expectError(ChangeSetPersister.NotFoundException.class)
                .verify()
    }


}


