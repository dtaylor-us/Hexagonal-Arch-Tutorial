package us.dtaylor.todoservice.ports;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import us.dtaylor.todoservice.domain.Todo;

@Repository
public interface TodoRepository extends ReactiveMongoRepository<Todo, String> {
    // Custom queries if needed
    Flux<Todo> findAllByUserId(String userId);
}
