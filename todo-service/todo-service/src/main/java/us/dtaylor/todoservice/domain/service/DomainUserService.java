package us.dtaylor.todoservice.domain.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;

import java.util.UUID;

public class DomainUserService implements UserService {

    private final UserService userService;

    public DomainUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<User> getUserById(UUID userId) {
        return userService.getUserById(userId);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
