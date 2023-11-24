package us.dtaylor.todoservice.domain.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;
import us.dtaylor.todoservice.infastructure.client.DeclarativeReactiveUserClient;

import java.util.UUID;

public class DomainUserService implements UserService {

    private final DeclarativeReactiveUserClient userClient;

    public DomainUserService(DeclarativeReactiveUserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public Mono<User> getUserById(UUID userId) {
        return userClient.getUserById(userId);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userClient.getAllUsers();
    }
}
