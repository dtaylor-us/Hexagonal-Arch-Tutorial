package us.dtaylor.todoservice.domain


import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject
import us.dtaylor.todoservice.domain.repository.TodoRepository
import us.dtaylor.todoservice.domain.service.DomainTodoService
import us.dtaylor.todoservice.infastructure.client.ReactiveUserClient
import us.dtaylor.todoservice.infastructure.persistence.repository.MongoDbTodoRepository

class DomainTodoServiceSpec extends Specification {

    public static final UUID USER_ID = UUID.randomUUID()

    @Subject
    DomainTodoService todoService

    TodoRepository todoRepository = Mock()

    ReactiveUserClient userClient = Mock()

    def setup() {
        todoService = new DomainTodoService(todoRepository, userClient)
    }

    def getTodo() {
        return new Todo(id: UUID.randomUUID(), title: "Test", description: "Test", completed: false, userId: USER_ID)
    }

    def "create todo item"() {
        given:
        def todo = getTodo()

        userClient.getUserById(USER_ID) >> Mono.just(new User(USER_ID, "Test", "Test"))

        when:
        todoRepository.save(todo) >> Mono.just(todo)

        then:
        StepVerifier.create(todoService.createTodo(todo))
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

    def "delete todo item - success"() {
        given:
        // Mock the deleteById method to return an empty Mono
        UUID todoId = getTodo().getId()
        todoRepository.deleteById(todoId) >> Mono.empty()

        when:
        // Call the deleteTodo service method
        Mono<Void> result = todoService.deleteTodo(todoId)

        then:
        // Verify that the Mono completes successfully
        StepVerifier.create(result)
                .verifyComplete()
    }

}


