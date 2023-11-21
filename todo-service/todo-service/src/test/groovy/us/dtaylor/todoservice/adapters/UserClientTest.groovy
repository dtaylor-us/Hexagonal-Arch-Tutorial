package us.dtaylor.todoservice.adapters


import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification
import us.dtaylor.todoservice.domain.User

class UserClientTest extends Specification {

    WebClient webClient;

    UserClient userClient;

    def setup() {
        webClient = Mock(WebClient)
        userClient = new UserClient(webClient)
    }

    def "getUserById should retrieve user"() {
        given:
        def userId = "123"
        def expectedUser = new User(userId, "John Doe", "john@example.com")
        def uriSpec = Mock(WebClient.RequestHeadersUriSpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        webClient.get() >> uriSpec
        uriSpec.uri("/api/v1/users/{id}", userId) >> headersSpec
        headersSpec.retrieve() >> responseSpec
        responseSpec.bodyToMono(User) >> Mono.just(expectedUser)

        when:
        Mono<User> result = userClient.getUserById(userId)

        then:
        result.block() == expectedUser
    }

    def "getUserById should return null if user not found"() {
        given:
        def userId = "123"
        def uriSpec = Mock(WebClient.RequestHeadersUriSpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        webClient.get() >> uriSpec
        uriSpec.uri("/api/v1/users/{id}", userId) >> headersSpec
        headersSpec.retrieve() >> responseSpec
        responseSpec.bodyToMono(User) >> Mono.empty()

        when:
        Mono<User> result = userClient.getUserById(userId)

        then:
        result.block() == null
    }

    def "updateUser should send a PUT request and return updated user"() {
        given:
        def userId = "123"
        def existingUser = new User(userId, "John Doe", "john@example.com")
        def updatedUser = new User(userId, "Jane Doe", "jane@example.com")
        def uriSpec = Mock(WebClient.RequestBodyUriSpec)
        def bodySpec = Mock(WebClient.RequestBodySpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        webClient.put() >> uriSpec
        uriSpec.uri("/api/v1/users/{id}", userId) >> bodySpec
        bodySpec.body(_, _) >> headersSpec
        headersSpec.retrieve() >> responseSpec
        responseSpec.bodyToMono(User.class) >> Mono.just(updatedUser)

        when:
        Mono<User> result = userClient.updateUser(userId, existingUser)

        then:
        result.block() == updatedUser
    }

    def "deleteUser should send a DELETE request and return void"() {
        given:
        def userId = "123"
        def uriSpec = Mock(WebClient.RequestHeadersUriSpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        webClient.delete() >> uriSpec
        uriSpec.uri("/api/v1/users/{id}", userId) >> headersSpec
        headersSpec.retrieve() >> responseSpec
        responseSpec.bodyToMono(Void) >> Mono.empty()

        when:
        Mono<Void> result = userClient.deleteUser(userId)

        then:
        result.block() == null
    }

    def "getAllUsers should send a GET request and return a Flux of users"() {
        given:
        def user1 = new User("123", "John Doe", "john@example.com")
        def user2 = new User("456", "Jane Doe", "jane@example.com")
        def uriSpec = Mock(WebClient.RequestHeadersUriSpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        webClient.get() >> uriSpec
        uriSpec.uri("/api/v1/users") >> headersSpec
        headersSpec.retrieve() >> responseSpec
        responseSpec.bodyToFlux(User) >> Flux.just(user1, user2)

        when:
        Flux<User> result = userClient.getAllUsers()

        then:
        result.collectList().block() == [user1, user2]
    }


}
