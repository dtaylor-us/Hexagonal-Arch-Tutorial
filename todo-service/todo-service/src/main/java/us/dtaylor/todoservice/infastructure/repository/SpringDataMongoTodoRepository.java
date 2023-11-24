package us.dtaylor.todoservice.infastructure.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import us.dtaylor.todoservice.domain.Todo;

@Repository
public interface SpringDataMongoTodoRepository extends ReactiveMongoRepository<Todo, String> {
    Flux<Todo> findAllByUserId(String userId);
}
