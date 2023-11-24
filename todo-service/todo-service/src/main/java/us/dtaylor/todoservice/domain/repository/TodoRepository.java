package us.dtaylor.todoservice.domain.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;

import java.util.UUID;

public interface TodoRepository {

    Flux<Todo> findAllByUserId(UUID userId);

    Mono<Void> deleteAll();

    Mono<Todo> save(Todo todo);

    Flux<Todo> findAll();

    Mono<Void> deleteById(UUID id);

    Mono<Todo> findById(UUID id);
}
