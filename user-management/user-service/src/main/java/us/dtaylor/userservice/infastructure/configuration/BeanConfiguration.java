package us.dtaylor.userservice.infastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import us.dtaylor.userservice.domain.repository.UserRepository;
import us.dtaylor.userservice.domain.service.DomainUserService;
import us.dtaylor.userservice.domain.service.UserService;

@Configuration
@ComponentScan(basePackageClasses = UserService.class)
public class BeanConfiguration {

    @Bean
    UserService userService(final UserRepository userRepository) {
        return new DomainUserService(userRepository);
    }

}
