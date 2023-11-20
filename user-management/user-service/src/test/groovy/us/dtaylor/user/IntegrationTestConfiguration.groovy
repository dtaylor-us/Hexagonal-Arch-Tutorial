package us.dtaylor.user

import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory
import us.dtaylor.user.ports.UserService

class IntegrationTestConfiguration {
    private final detachedMockFactory = new DetachedMockFactory()

    @Bean
    UserService userService() {
        detachedMockFactory.Mock(UserService)
    }
}
