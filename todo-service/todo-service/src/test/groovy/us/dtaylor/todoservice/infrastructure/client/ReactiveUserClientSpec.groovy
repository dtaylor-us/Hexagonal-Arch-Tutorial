package us.dtaylor.todoservice.infrastructure.client


import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification
import us.dtaylor.todoservice.domain.User
import us.dtaylor.todoservice.domain.exceptions.ClientException
import us.dtaylor.todoservice.infastructure.client.ReactiveUserClient

class ReactiveUserClientSpec extends Specification {

    WebClient userWebClient;

    ReactiveUserClient userClient;

    def setup() {
        userWebClient = Mock(WebClient)
        userClient = new ReactiveUserClient(userWebClient)
    }

    def "getUserById should retrieve user"() {
        given:
        def userId = UUID.randomUUID()
        def expectedUser = new User(id: userId, name: "John Doe", email: "john@example.com")
        def uriSpec = Mock(WebClient.RequestHeadersUriSpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        userWebClient.get() >> uriSpec
        uriSpec.uri("/api/v1/users/{id}", userId) >> headersSpec
        headersSpec.retrieve() >> responseSpec

        // Use argument matchers to ensure that onStatus handles any arguments appropriately
        responseSpec.onStatus({ it -> true }, { _ -> Mono.error(new ClientException("Error")) }) >> responseSpec
        responseSpec.bodyToMono(User) >> Mono.just(expectedUser)

        when:
        Mono<User> result = userClient.getUserById(userId)

        then:
        result.block() == expectedUser
    }

    def "getUserById should return null if user not found"() {
        given:
        def userId = UUID.randomUUID()
        def uriSpec = Mock(WebClient.RequestHeadersUriSpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        userWebClient.get() >> uriSpec
        uriSpec.uri("/api/v1/users/{id}", userId) >> headersSpec
        headersSpec.retrieve() >> responseSpec
        responseSpec.onStatus({ it -> true }, { _ -> Mono.error(new ClientException("Error")) }) >> responseSpec
        responseSpec.bodyToMono(User) >> Mono.empty()

        when:
        Mono<User> result = userClient.getUserById(userId)

        then:
        result.block() == null
    }

    def "getAllUsers should send a GET request and return a Flux of users"() {
        given:
        def user1 = new User(id: UUID.randomUUID(), name: "John Doe", email: "john@example.com")
        def user2 = new User(id: UUID.randomUUID(), name: "Jane Doe", email: "jane@example.com")
        def uriSpec = Mock(WebClient.RequestHeadersUriSpec)
        def headersSpec = Mock(WebClient.RequestHeadersSpec)
        def responseSpec = Mock(WebClient.ResponseSpec)

        userWebClient.get() >> uriSpec
        uriSpec.uri("/api/v1/users") >> headersSpec
        headersSpec.retrieve() >> responseSpec
        responseSpec.onStatus({ it -> true }, { _ -> Mono.error(new ClientException("Error")) }) >> responseSpec

        responseSpec.bodyToFlux(User) >> Flux.just(user1, user2)

        when:
        Flux<User> result = userClient.getAllUsers()

        then:
        result.collectList().block() == [user1, user2]
    }


}
