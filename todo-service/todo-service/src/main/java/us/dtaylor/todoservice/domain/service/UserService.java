package us.dtaylor.todoservice.domain.service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;

import java.util.UUID;

public interface UserService {
    Mono<User> getUserById(UUID userId);

    Flux<User> getAllUsers();
}
