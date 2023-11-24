package us.dtaylor.userservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import us.dtaylor.userservice.domain.User;
import us.dtaylor.userservice.infastructure.persistence.repository.MongoDbUserRepository;

import java.util.UUID;

@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    @Bean
    @Profile("db-seed")
    public CommandLineRunner databaseSeeder(MongoDbUserRepository mongoDbUserRepository) {
        return args -> {
            // Check if the database is empty
                mongoDbUserRepository.deleteAll().block();
                // Seed the database
                mongoDbUserRepository.save(new User(UUID.randomUUID(), "admin", "admin@todo-app.com")).block();
        };
    }
}
