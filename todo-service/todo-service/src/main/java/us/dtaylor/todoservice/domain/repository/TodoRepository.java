package us.dtaylor.todoservice.domain.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;

public interface TodoRepository {

    Flux<Todo> findAllByUserId(String userId);

    Mono<Void> deleteAll();

    Mono<Todo> save(Todo todo);

    Flux<Todo> findAll();

    Mono<Void> deleteById(String id);

    Mono<Todo> findById(String id);
}
