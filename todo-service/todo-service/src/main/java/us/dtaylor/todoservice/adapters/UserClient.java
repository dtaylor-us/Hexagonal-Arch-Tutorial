package us.dtaylor.todoservice.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;

@Component
public class UserClient {

    private final WebClient userWebClient;

    @Autowired
    public UserClient(WebClient userWebClient) {
        this.userWebClient = userWebClient;
    }


    public Mono<User> getUserById(String userId) {
        return userWebClient.get()
                .uri("/api/v1/users/{id}", userId)
                .retrieve()
                .bodyToMono(User.class);
    }


    // Other methods like updateUser, deleteUser etc.
    public Mono<User> updateUser(String userId, User user) {
        return userWebClient.put()
                .uri("/api/v1/users/{id}", userId)
                .body(Mono.just(user), User.class)
                .retrieve()
                .bodyToMono(User.class);
    }

    public Mono<Void> deleteUser(String userId) {
        return userWebClient.delete()
                .uri("/api/v1/users/{id}", userId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Flux<User> getAllUsers() {
        return userWebClient.get()
                .uri("/api/v1/users")
                .retrieve()
                .bodyToFlux(User.class);
    }
}
