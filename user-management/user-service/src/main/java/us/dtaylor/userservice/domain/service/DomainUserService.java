package us.dtaylor.userservice.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.userservice.domain.User;
import us.dtaylor.userservice.domain.repository.UserRepository;

import java.util.UUID;

@Service
public class DomainUserService implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public DomainUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> updateUser(UUID id, User user) {
        return userRepository.findById(id)
                .flatMap(existingTodo -> {
                    existingTodo.setName(user.getName());
                    existingTodo.setEmail(user.getEmail());
                    return userRepository.save(existingTodo);
                })
                .switchIfEmpty(Mono.error(new ChangeSetPersister.NotFoundException()));
    }

    @Override
    public Mono<Void> deleteUser(UUID id) {
        return userRepository.findById(id)
                .flatMap(userRepository::delete);
    }
}
