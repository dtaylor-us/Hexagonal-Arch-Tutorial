package us.dtaylor.todoservice.ports;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;

public interface TodoService {
    Mono<Todo> createTodo(String userId, Todo todo);
    Flux<Todo> getAllTodosByUserId(String userId);
    Flux<Todo> getAllTodos();
    Mono<Todo> getTodoById(String id);
    Mono<Todo> updateTodo(String id, Todo todo);
    Mono<Void> deleteTodo(String id);
}
