package us.dtaylor.todoservice.infastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import us.dtaylor.todoservice.domain.repository.TodoRepository;
import us.dtaylor.todoservice.domain.service.DomainTodoService;
import us.dtaylor.todoservice.domain.service.DomainUserService;
import us.dtaylor.todoservice.domain.service.TodoService;
import us.dtaylor.todoservice.domain.service.UserService;
import us.dtaylor.todoservice.infastructure.client.DeclarativeUserClient;

@Configuration
@ComponentScan(basePackageClasses = TodoService.class)
public class BeanConfiguration {

    @Bean
    TodoService todoService(final TodoRepository todoRepository, final UserService userService) {
        return new DomainTodoService(todoRepository, userService);
    }

    @Bean
    UserService userService(final DeclarativeUserClient userClient) {
        return new DomainUserService(userClient);
    }
}
