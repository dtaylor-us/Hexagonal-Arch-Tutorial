package us.dtaylor.todoservice.ports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;

@Service
public class TodoServiceImpl implements TodoService {
    private final TodoRepository repository;

    @Autowired
    public TodoServiceImpl(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Todo> createTodo(Todo todo) {
        return repository.save(todo);
    }

    @Override
    public Flux<Todo> getAllTodos() {
        return repository.findAll();
    }

    @Override
    public Mono<Todo> getTodoById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Todo> updateTodo(String id, Todo todo) {
        return repository.findById(id)
                .flatMap(existingTodo -> {
                    existingTodo.setTitle(todo.getTitle());
                    existingTodo.setDescription(todo.getDescription());
                    existingTodo.setCompleted(todo.isCompleted());
                    return repository.save(existingTodo);
                })
                .switchIfEmpty(Mono.error(new ChangeSetPersister.NotFoundException()));
    }


    @Override
    public Mono<Void> deleteTodo(String id) {
        return repository.findById(id)
                .flatMap(repository::delete)
                .switchIfEmpty(Mono.error(new ChangeSetPersister.NotFoundException()));
    }


}
