package us.dtaylor.todoservice.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.repository.TodoRepository;
import us.dtaylor.todoservice.domain.Todo;

@Service
public class DomainTodoService implements TodoService {
    private final TodoRepository repository;
    private final UserService userService;

    @Autowired
    public DomainTodoService(TodoRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Mono<Todo> createTodo(String userId, Todo todo) {
        return userService.getUserById(userId)
                .flatMap(user -> {
                    todo.setUserId(user.getId());
                    return repository.save(todo);
                });
    }


    @Override
    public Flux<Todo> getAllTodosByUserId(String userId) {
        return repository.findAllByUserId(userId);
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
        return repository.deleteById(id);
    }


}
