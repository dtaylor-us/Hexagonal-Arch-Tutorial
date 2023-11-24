package us.dtaylor.todoservice.infastructure.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import us.dtaylor.todoservice.infastructure.persistence.TodoDocument;

@Repository
public interface SpringDataMongoTodoRepository extends ReactiveMongoRepository<TodoDocument, String> {
    Flux<TodoDocument> findAllByUserId(String userId);
}
