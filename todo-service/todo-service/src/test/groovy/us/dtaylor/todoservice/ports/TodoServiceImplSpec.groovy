package us.dtaylor.todoservice.ports


import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject
import us.dtaylor.todoservice.adapters.UserClient
import us.dtaylor.todoservice.domain.Todo
import us.dtaylor.todoservice.domain.User

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
        return new Todo(id: "1", title: "Test", description: "Test", completed: false, userId: USER_ID)
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

    def "delete todo item - success"() {
        given:
        todoRepository.deleteById("1") >> Mono.just(Void)

        when:
        Mono<Void> result = todoService.deleteTodo("1")

        then:
        StepVerifier.create(result)
        .expectNextMatches { //noinspection GrEqualsBetweenInconvertibleTypes
            it == Void }
                .verifyComplete()
    }

}


