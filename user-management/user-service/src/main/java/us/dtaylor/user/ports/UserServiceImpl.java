package us.dtaylor.user.ports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.user.domain.User;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
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
    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Mono<User> updateUser(String id, User user) {
        return userRepository.findById(id)
                .flatMap(existingTodo -> {
                    existingTodo.setName(user.getName());
                    existingTodo.setEmail(user.getEmail());
                    return userRepository.save(existingTodo);
                })
                .switchIfEmpty(Mono.error(new ChangeSetPersister.NotFoundException()));
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return userRepository.findById(id)
                .flatMap(userRepository::delete)
                .switchIfEmpty(Mono.error(new ChangeSetPersister.NotFoundException()));
    }
}
