package us.dtaylor.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import us.dtaylor.user.domain.User;
import us.dtaylor.user.ports.UserRepository;

@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    @Profile("db-seed")
    public CommandLineRunner databaseSeeder(UserRepository userRepository) {
        return args -> {
            // Check if the database is empty
            if (userRepository.count().block() == 0L) {
                userRepository.deleteAll().block();
                // Seed the database
                userRepository.save(new User("1", "admin", "admin@todo-app.com")).block();
            }
        };
    }
}
