package us.dtaylor.todoservice.infastructure.persistence.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;
import us.dtaylor.todoservice.domain.repository.TodoRepository;
import us.dtaylor.todoservice.infastructure.persistence.TodoDocument;

import java.util.UUID;

@Component
@Primary
public class MongoDbTodoRepository implements TodoRepository {
    private final SpringDataMongoTodoRepository repository;

    public MongoDbTodoRepository(SpringDataMongoTodoRepository repository) {
        this.repository = repository;
    }
    @Override
    public Flux<Todo> findAllByUserId(UUID userId) {
        return repository.findAllByUserId(userId.toString())
                .map(TodoDocument::toDomain);
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    @Override
    public Mono<Todo> save(Todo todo) {
        return repository.save(TodoDocument.toDocument(todo))
                .map(TodoDocument::toDomain);
    }

    @Override
    public Flux<Todo> findAll() {
        return repository.findAll()
                .map(TodoDocument::toDomain);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id.toString());
    }

    @Override
    public Mono<Todo> findById(UUID id) {
        return repository.findById(id.toString()).map(TodoDocument::toDomain);
    }
}
