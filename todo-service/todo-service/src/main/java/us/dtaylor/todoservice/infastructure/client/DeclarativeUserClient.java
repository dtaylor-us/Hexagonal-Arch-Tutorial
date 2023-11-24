package us.dtaylor.todoservice.infastructure.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.User;
import us.dtaylor.todoservice.domain.service.UserService;

public interface DeclarativeUserClient extends UserService {

    @Override
    @GetExchange("/api/v1/users/{userId}")
    Mono<User> getUserById(@PathVariable("userId") String userId);

}
