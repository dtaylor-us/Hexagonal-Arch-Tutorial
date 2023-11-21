package us.dtaylor.todoservice.ports

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.crossstore.ChangeSetPersister
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject
import us.dtaylor.todoservice.adapters.UserClient
import us.dtaylor.todoservice.domain.Todo
import us.dtaylor.todoservice.domain.User

@SpringBootTest
class TodoServiceImplSpec extends Specification {

    public static final String USER_ID = "2"

    @Subject
    TodoServiceImpl todoService

    TodoRepository todoRepository = Mock()

    UserClient userClient = Mock()

    def setup() {
        todoService = new TodoServiceImpl(todoRepository, userClient)
    }

    def getTodo() {
        return Todo.builder()
                .id("1")
                .title("Test")
                .description("Test")
                .completed(false)
                .userId(USER_ID)
                .build()
    }

    def "create todo item"() {
        given:
        def todo = getTodo()

        userClient.getUserById(USER_ID) >> Mono.just(new User(USER_ID, "Test", "Test"))

        when:
        todoRepository.save(todo) >> Mono.just(todo)

        then:
        StepVerifier.create(todoService.createTodo(USER_ID, todo))
                .expectNextMatches {
                    it.title == "Test" && it.getUserId() == USER_ID
                }
                .verifyComplete()

    }

    def "get all todo items"() {
        given:
        def todo = getTodo()

        when:
        todoRepository.findAll() >> Flux.just(todo)

        then:
        StepVerifier.create(todoService.getAllTodos())
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

    def "get todo item by id"() {
        given:
        def todo = getTodo()

        when:
        todoRepository.findById(todo.id) >> Mono.just(todo)

        then:
        StepVerifier.create(todoService.getTodoById(todo.id))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

    def "update todo item"() {
        given:
        def todo = getTodo()

        when:
        todoRepository.findById(todo.id) >> Mono.just(todo)
        todoRepository.save(todo) >> Mono.just(todo)

        then:
        StepVerifier.create(todoService.updateTodo(todo.id, todo))
                .expectNextMatches { it.title == "Test" }
                .verifyComplete()
    }

//    TODO: FIX TEST
//    def "delete todo item - success"() {
//        given:
//        def todo = getTodo()
//        todoRepository.findById(todo.id) >> Mono.just(todo) // Make sure this is the expected todo
//        todoRepository.delete(todo) >> Mono.just(Void) // Correctly mock the delete operation
//
//        when:
//        Mono<Void> result = todoService.deleteTodo(todo.id)
//
//        then:
//        StepVerifier.create(result)
//                .expectNextMatches { it == null }
//                .verifyComplete() // Verify that the Mono completes without emitting any item
//    }

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


