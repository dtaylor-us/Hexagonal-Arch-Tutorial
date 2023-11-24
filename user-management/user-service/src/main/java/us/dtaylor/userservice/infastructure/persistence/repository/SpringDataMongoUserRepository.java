package us.dtaylor.userservice.infastructure.persistence.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import us.dtaylor.userservice.infastructure.UserDocument;

@Repository
public interface SpringDataMongoUserRepository extends ReactiveMongoRepository<UserDocument, String> {
    // Custom queries if needed
}

