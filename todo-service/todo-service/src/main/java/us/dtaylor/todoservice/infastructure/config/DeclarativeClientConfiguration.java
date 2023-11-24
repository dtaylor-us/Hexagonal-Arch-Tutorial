package us.dtaylor.todoservice.infastructure.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import us.dtaylor.todoservice.domain.exceptions.ClientException;
import us.dtaylor.todoservice.domain.exceptions.ClientTimeOutException;
import us.dtaylor.todoservice.infastructure.client.DeclarativeReactiveUserClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class DeclarativeClientConfiguration {

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Bean
    public WebClientAdapter userWebClientAdapter() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(300, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(300, TimeUnit.SECONDS)));

        WebClient webClient = WebClient.builder()
                .baseUrl(userServiceUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(timeoutExceptionFilter())
                .filter(clientErrorExceptionFilter())
                .build();
       return WebClientAdapter.forClient(webClient);
    }


    private ExchangeFilterFunction timeoutExceptionFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new ClientTimeOutException("Service unavailable")));
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction clientErrorExceptionFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new ClientException("Client error")));
            }
            return Mono.just(clientResponse);
        });
    }

    @Bean
    DeclarativeReactiveUserClient userClient(WebClientAdapter userWebClientAdapter) {
    return HttpServiceProxyFactory.builder(userWebClientAdapter)
            .build()
            .createClient(DeclarativeReactiveUserClient.class);
        }

}
