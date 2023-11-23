package us.dtaylor.todoservice.ports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.adapters.UserClient;
import us.dtaylor.todoservice.domain.Todo;
import us.dtaylor.todoservice.exceptions.UserNotFoundException;

@Service
public class TodoServiceImpl implements TodoService {
    private final TodoRepository repository;
    private final UserClient userClient;

    @Autowired
    public TodoServiceImpl(TodoRepository repository, UserClient userClient) {
        this.repository = repository;
        this.userClient = userClient;
    }

    @Override
    public Mono<Todo> createTodo(String userId, Todo todo) {
        return userClient.getUserById(userId)
                .flatMap(user -> {
                    todo.setUserId(user.getId());
                    return repository.save(todo);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("User not found for id: " + userId)));

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
