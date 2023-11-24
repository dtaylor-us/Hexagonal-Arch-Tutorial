package us.dtaylor.userservice

import org.springframework.context.annotation.Bean
import spock.mock.DetachedMockFactory
import us.dtaylor.userservice.domain.service.UserService

class IntegrationTestConfiguration {
    private final detachedMockFactory = new DetachedMockFactory()

    @Bean
    UserService userService() {
        detachedMockFactory.Mock(UserService)
    }
}
