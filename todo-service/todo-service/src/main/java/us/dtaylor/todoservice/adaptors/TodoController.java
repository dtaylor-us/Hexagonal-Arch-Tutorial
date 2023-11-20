package us.dtaylor.todoservice.adaptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;
import us.dtaylor.todoservice.ports.TodoService;

@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public Mono<ResponseEntity<Todo>> createTodo(@RequestBody Todo todo) {
        return todoService.createTodo(todo)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Todo>>> getAllTodos() {
        Flux<Todo> todoFlux = todoService.getAllTodos();
        return Mono.just(ResponseEntity.ok().body(todoFlux));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Todo>> findById(@PathVariable String id) {
        return todoService.getTodoById(id)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Todo>> updateById(@PathVariable String id, @RequestBody Todo todo) {
        return todoService.updateTodo(id, todo)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id) {
        return todoService.deleteTodo(id).then(Mono.just(ResponseEntity.ok().build()));
    }
}
