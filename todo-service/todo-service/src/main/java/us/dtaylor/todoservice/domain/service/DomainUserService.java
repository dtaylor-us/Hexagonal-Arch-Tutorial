package us.dtaylor.todoservice.domain.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;
import us.dtaylor.todoservice.infastructure.client.DeclarativeUserClient;

public class DomainUserService implements UserService {

    private final DeclarativeUserClient userClient;

    public DomainUserService(DeclarativeUserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public Mono<User> getUserById(String userId) {
        return userClient.getUserById(userId);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userClient.getAllUsers();
    }
}
