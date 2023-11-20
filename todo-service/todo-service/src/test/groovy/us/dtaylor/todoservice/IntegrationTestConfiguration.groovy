package us.dtaylor.todoservice

import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory
import us.dtaylor.todoservice.ports.TodoService


class IntegrationTestConfiguration {
    private final detachedMockFactory = new DetachedMockFactory()

    @Bean
    TodoService todoService() {
        detachedMockFactory.Mock(TodoService)
    }
}
