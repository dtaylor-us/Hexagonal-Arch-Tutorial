package us.dtaylor.todoservice.infastructure.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;
import us.dtaylor.todoservice.domain.exceptions.ClientException;
import us.dtaylor.todoservice.domain.exceptions.ClientTimeOutException;
import us.dtaylor.todoservice.domain.service.UserService;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
public class ReactiveUserClient implements UserService {

    private static final String ERROR_OCCURRED_RETRIEVING_USER = "Error occurred retrieving user";
    private final WebClient userWebClient;

    @Autowired
    public ReactiveUserClient(WebClient userWebClient) {
        this.userWebClient = userWebClient;
    }

    public Mono<User> getUserById(String userId) {
        return userWebClient.get().uri("/api/v1/users/{id}", userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ClientException(ERROR_OCCURRED_RETRIEVING_USER)))
                .bodyToMono(User.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(TimeoutException.class, e -> new ClientTimeOutException("Service unavailable"));
    }

    public Flux<User> getAllUsers() {
        return userWebClient.get().uri("/api/v1/users")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new ClientException(ERROR_OCCURRED_RETRIEVING_USER)))
                .bodyToFlux(User.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorMap(TimeoutException.class, e -> new ClientTimeOutException("Service unavailable"));
    }

}
