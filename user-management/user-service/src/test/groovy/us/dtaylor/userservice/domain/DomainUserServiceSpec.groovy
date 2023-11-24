package us.dtaylor.userservice.domain

import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject
import us.dtaylor.userservice.domain.repository.UserRepository
import us.dtaylor.userservice.domain.service.DomainUserService

@SpringBootTest
class DomainUserServiceSpec extends Specification {

    public static final String EMAIL = "spock@email.com"
    @Subject
    DomainUserService userService

    UserRepository userRepository = Mock()

    def setup() {
        userService = new DomainUserService(userRepository)
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

    def "get user by id"() {
        given:
        def user = new User(
                id: UUID.randomUUID(),
                name: "Test",
                email: EMAIL)
        when: "getById is called"
        userRepository.findById(user.id) >> Mono.just(user)

        then: "the user is returned"
        StepVerifier.create(userService.getUserById(user.id))
                .expectNextMatches { it.name == "Test" }
                .verifyComplete()
    }

    def "create user"() {
        given:
        def user = new User(
                name: "Test",
                email: EMAIL)
        when: "createUser is called"
        userRepository.save(user) >> Mono.just(user)

        then: "the user is returned"
        StepVerifier.create(userService.createUser(user))
                .expectNextMatches { it.name == "Test" }
                .verifyComplete()
    }

    def "update user"() {
        given:
        def id = UUID.randomUUID()

        def user = new User(
                id: id,
                name: "Test",
                email: EMAIL)

        userRepository.findById(id) >> Mono.just(user)

        when: "updateUser is called"
        userRepository.save(user) >> Mono.just(user)

        then: "the user is returned"
        StepVerifier.create(userService.updateUser(id, user))
                .expectNextMatches { it.name == user.name }
                .verifyComplete()
    }

}
