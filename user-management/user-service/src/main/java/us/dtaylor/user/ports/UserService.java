package us.dtaylor.user.ports;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.user.domain.User;

public interface UserService {
    Mono<User> createUser(User user);

    Flux<User> getAllUsers();

    Mono<User> getUserById(String id);

    Mono<User> updateUser(String id, User user);

    Mono<Void> deleteUser(String id);
}
