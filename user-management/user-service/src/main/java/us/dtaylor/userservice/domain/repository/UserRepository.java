package us.dtaylor.userservice.domain.repository;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.userservice.domain.User;

import java.util.UUID;

public interface UserRepository {
    Mono<Void> deleteAll();

    Mono<User> save(User user);

    Flux<User> findAll();

    Mono<User> findById(UUID id);

    Mono<Void> delete(User user);
}
