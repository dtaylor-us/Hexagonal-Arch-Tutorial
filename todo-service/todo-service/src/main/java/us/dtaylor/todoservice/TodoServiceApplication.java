package us.dtaylor.todoservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import us.dtaylor.todoservice.domain.Todo;
import us.dtaylor.todoservice.infastructure.persistence.repository.MongoDbTodoRepository;

import java.util.UUID;

@SpringBootApplication
public class TodoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoServiceApplication.class, args);
    }

    @Bean
    @Profile("db-seed")
    public CommandLineRunner databaseSeeder(MongoDbTodoRepository todoRepository) {
        return args -> {
            // Check if the database is empty
                todoRepository.deleteAll().block();
                // Seed the database
                todoRepository.save(new Todo(UUID.randomUUID(), "Task 1", "Description 1", false, UUID.randomUUID())).block();
                todoRepository.save(new Todo(UUID.randomUUID(), "Task 2", "Description 2", true, UUID.randomUUID())).block();
                // Add more todos as needed
        };
    }
}
