package us.dtaylor.userservice.infastructure.persistence.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.userservice.domain.User;
import us.dtaylor.userservice.domain.repository.UserRepository;
import us.dtaylor.userservice.infastructure.UserDocument;

import java.util.UUID;

@Component
@Primary
public class MongoDbUserRepository implements UserRepository {

    private final SpringDataMongoUserRepository repository;

    public MongoDbUserRepository(SpringDataMongoUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }

    @Override
    public Mono<User> save(User user) {
        return repository.save(new UserDocument(user))
                .map(UserDocument::toDomain);
    }

    @Override
    public Flux<User> findAll() {
        return repository.findAll()
                .map(UserDocument::toDomain);
    }

    @Override
    public Mono<User> findById(UUID id) {
        return repository.findById(id.toString())
                .map(UserDocument::toDomain);
    }

    @Override
    public Mono<Void> delete(User user) {
        return repository.delete(new UserDocument(user));
    }
}
