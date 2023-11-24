package us.dtaylor.todoservice.domain.service;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;

public interface UserService {
    Mono<User> getUserById(String userId);

    Flux<User> getAllUsers();
}
