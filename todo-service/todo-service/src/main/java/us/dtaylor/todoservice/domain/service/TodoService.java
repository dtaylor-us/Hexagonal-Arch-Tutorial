package us.dtaylor.todoservice.domain.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;

import java.util.UUID;

public interface TodoService {
    Mono<Todo> createTodo(Todo todo);
    Flux<Todo> getAllTodosByUserId(UUID userId);
    Flux<Todo> getAllTodos();
    Mono<Todo> getTodoById(UUID id);
    Mono<Todo> updateTodo(UUID id, Todo todo);
    Mono<Void> deleteTodo(UUID id);
}
