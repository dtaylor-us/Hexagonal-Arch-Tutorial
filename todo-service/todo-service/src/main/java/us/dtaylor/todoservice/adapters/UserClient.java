package us.dtaylor.todoservice.adapters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;
import us.dtaylor.todoservice.exceptions.UserClientException;

import java.time.Duration;

@Component
public class UserClient {

    private static final String ERROR_OCCURRED_RETRIEVING_USER = "Error occurred retrieving user";
    private final WebClient userWebClient;

    @Autowired
    public UserClient(WebClient userWebClient) {
        this.userWebClient = userWebClient;
    }

    public Mono<User> getUserById(String userId) {
        return makeRequest(userWebClient.get().uri("/api/v1/users/{id}", userId), User.class);
    }

    public Mono<User> updateUser(String userId, User user) {
        return makeRequest(userWebClient.put().uri("/api/v1/users/{id}", userId).body(Mono.just(user), User.class), User.class);
    }

    public Mono<Void> deleteUser(String userId) {
        return makeRequest(userWebClient.delete().uri("/api/v1/users/{id}", userId), Void.class);
    }

    public Flux<User> getAllUsers() {
        return makeRequest(userWebClient.get().uri("/api/v1/users"), User.class).flux();
    }

    private <T> Mono<T> makeRequest(WebClient.RequestHeadersSpec<?> request, Class<T> bodyType) {
        return request
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new UserClientException(ERROR_OCCURRED_RETRIEVING_USER)))
                .bodyToMono(bodyType)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> Mono.error(new UserClientException("Service unavailable")));
    }
}
