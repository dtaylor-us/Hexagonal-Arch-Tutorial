package us.dtaylor.user.ports

import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject
import us.dtaylor.user.domain.User

@SpringBootTest
class UserServiceSpecImpl extends Specification {

    @Subject
    UserServiceImpl userService

    UserRepository userRepository = Mock()

    def setup() {
        userService = new UserServiceImpl(userRepository)
    }

    def "get all users"() {
        given:
        def user = new User(
                name: "Test",
                email: "test@spock.com")
        when:
        userRepository.findAll() >> Flux.just(user)

        then:
        StepVerifier.create(userService.getAllUsers())
                .expectNextMatches { it.name == "Test" }
                .verifyComplete()
    }
}
