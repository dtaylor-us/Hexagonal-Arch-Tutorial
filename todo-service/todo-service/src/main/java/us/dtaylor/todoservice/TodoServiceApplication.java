package us.dtaylor.todoservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import us.dtaylor.todoservice.domain.Todo;
import us.dtaylor.todoservice.ports.TodoRepository;

@SpringBootApplication
public class TodoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoServiceApplication.class, args);
    }

    @Bean
    @Profile("db-seed")
    public CommandLineRunner databaseSeeder(TodoRepository todoRepository) {
        return args -> {
            // Check if the database is empty
            if (todoRepository.count().block() == 0L) {
                todoRepository.deleteAll().block();
                // Seed the database
                todoRepository.save(new Todo("1", "Task 1", "Description 1", false)).block();
                todoRepository.save(new Todo("2", "Task 2", "Description 2", true)).block();
                // Add more todos as needed
            }
        };
    }
}
