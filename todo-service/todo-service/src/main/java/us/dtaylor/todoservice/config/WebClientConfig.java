package us.dtaylor.todoservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient userWebClient(@Value("${user.service.url}") String userServiceUrl) {
        return WebClient.builder().baseUrl(userServiceUrl).build();
    }
}
