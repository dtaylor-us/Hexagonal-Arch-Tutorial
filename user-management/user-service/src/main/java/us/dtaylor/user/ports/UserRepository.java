package us.dtaylor.user.ports;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import us.dtaylor.user.domain.User;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    // Custom queries if needed
}
