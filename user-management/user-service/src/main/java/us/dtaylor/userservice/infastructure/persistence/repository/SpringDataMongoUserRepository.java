package us.dtaylor.userservice.infastructure.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import us.dtaylor.userservice.infastructure.UserDocument;

@Repository
public interface SpringDataMongoUserRepository extends ReactiveMongoRepository<UserDocument, String> {
    Mono<UserDocument> findByName(String username);
    // Custom queries if needed
}

