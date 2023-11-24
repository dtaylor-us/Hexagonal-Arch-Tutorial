package us.dtaylor.userservice.domain.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.userservice.domain.User;

import java.util.UUID;

public interface UserService {
    Mono<User> createUser(User user);

    Flux<User> getAllUsers();

    Mono<User> getUserById(UUID id);

    Mono<User> updateUser(UUID id, User user);

    Mono<Void> deleteUser(UUID id);
}
