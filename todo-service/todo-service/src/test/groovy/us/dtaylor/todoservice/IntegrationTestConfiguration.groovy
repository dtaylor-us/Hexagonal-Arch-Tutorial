package us.dtaylor.todoservice

import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory
import us.dtaylor.todoservice.ports.TodoService

/**
 * A configuration class of mock for the integration test.
 *
 * @author Hidetake Iwata
 */
class IntegrationTestConfiguration {
    private final detachedMockFactory = new DetachedMockFactory()

    @Bean
    TodoService todoService() {
        detachedMockFactory.Mock(TodoService)
    }
}
