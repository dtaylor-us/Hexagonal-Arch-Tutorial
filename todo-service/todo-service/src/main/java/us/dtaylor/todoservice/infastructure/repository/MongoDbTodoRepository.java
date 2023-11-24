package us.dtaylor.todoservice.infastructure.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;
import us.dtaylor.todoservice.domain.repository.TodoRepository;

@Component
@Primary
public class MongoDbTodoRepository implements TodoRepository {
    private final SpringDataMongoTodoRepository repository;

    public MongoDbTodoRepository(SpringDataMongoTodoRepository repository) {
        this.repository = repository;
    }
    @Override
    public Flux<Todo> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    @Override
    public Mono<Todo> save(Todo todo) {
        return repository.save(todo);
    }

    @Override
    public Flux<Todo> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Mono<Todo> findById(String id) {
        return repository.findById(id);
    }
}
