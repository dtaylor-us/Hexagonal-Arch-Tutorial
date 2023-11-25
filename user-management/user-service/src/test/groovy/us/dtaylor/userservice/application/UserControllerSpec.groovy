package us.dtaylor.userservice.application

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification
import us.dtaylor.userservice.IntegrationTestConfiguration
import us.dtaylor.userservice.domain.User
import us.dtaylor.userservice.domain.service.UserService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class UserControllerSpec extends Specification {

    public static final String TEST_EMAIL = "spock@test.com"
    public static final UUID ID = UUID.randomUUID()
    public static final String NAME = "FooBar"

    @Autowired
    WebTestClient webTestClient

    @MockBean
    UserService userService

    def 'getAll should return list of users'() {
        given:
        def user = new User(
                name: NAME,
                email: "test@spock.com")
        userService.getAllUsers() >> Flux.just(user)

        when: "getAll endpoint is called"
        def response = webTestClient.get().uri("/api/v1/users")
                .exchange()

        then: "the response is successful and contains the users"
        response.expectStatus().isOk()
                .expectBodyList(User.class).hasSize(1)
                .consumeWith { result ->
                    assert result.responseBody[0].name == NAME
                    assert result.responseBody[0].email == "test@spock.com"
                }
    }

    def 'getById should return user by id'() {
        given:
        def user = new User(
                id: ID,
                name: NAME,
                email: TEST_EMAIL)
        userService.getUserById(user.id) >> Mono.just(user)

        when: "getById endpoint is called"
        def response = webTestClient.get().uri("/api/v1/users/${user.id.toString()}")
                .exchange()

        then: "the response is successful and contains the user"
        response.expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith { result ->
                    assert result.responseBody.name == NAME
                    assert result.responseBody.email == TEST_EMAIL
                }
    }

    // - create user
    def 'create should return created user'() {
        given:
        def user = new User(
                name: NAME,
                email: TEST_EMAIL)

        userService.createUser(_) >> Mono.just(user)

        when: "create endpoint is called"
        def response = webTestClient.post().uri("/api/v1/users")
                .bodyValue(user)
                .exchange()

        then: "the response is successful and contains the user"
        response.expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith { result ->
                    assert result.responseBody.name == NAME
                    assert result.responseBody.email == TEST_EMAIL
                }
    }

    // - update user
    def 'update should return updated user'() {
        given:
        def user = new User(
                id: ID,
                name: NAME,
                email: TEST_EMAIL)

        userService.updateUser(user.id, _ as User) >> Mono.just(user)

        when: "update endpoint is called"
        def response = webTestClient.put().uri("/api/v1/users/${user.id.toString()}")
                .bodyValue(user)
                .exchange()

        then: "the response is successful and contains the user"
        response.expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith { result ->
                    assert result.responseBody.name == NAME
                    assert result.responseBody.email == TEST_EMAIL
                }
    }

    // - delete user
    def 'delete should return no content'() {
        given:
        def user = new User(
                id: ID,
                name: NAME,
                email: TEST_EMAIL)
        userService.deleteUser(user.id) >> Mono.empty()

        when: "delete endpoint is called"
        def response = webTestClient.delete().uri("/api/v1/users/${user.id.toString()}")
                .exchange()

        then: "the response is successful and contains the user"
        response.expectStatus().isOk()
                .expectBody(Void.class)
    }
}
