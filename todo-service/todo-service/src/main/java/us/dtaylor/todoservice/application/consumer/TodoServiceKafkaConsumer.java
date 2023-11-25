package us.dtaylor.todoservice.application.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.Todo;
import us.dtaylor.todoservice.domain.service.TodoService;

import java.util.UUID;

@Slf4j
@Service
public class TodoServiceKafkaConsumer {

    private final TodoService todoService;

    public TodoServiceKafkaConsumer(TodoService todoService) {
        this.todoService = todoService;
    }

    @KafkaListener(topics = "todo-topic", groupId = "todo-group")
    public void listen(String message) {
        Mono.just(toDomain(message))
                .flatMap(todoService::createTodo)
                .subscribe();
    }

    private static Todo toDomain(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TodoMessage todoMessage = objectMapper.readValue(message, TodoMessage.class);
            return new Todo()
                    .setId(UUID.fromString(todoMessage.id()))
                    .setTitle(todoMessage.title())
                    .setDescription(todoMessage.description())
                    .setCompleted(todoMessage.completed())
                    .setUserId(UUID.fromString(todoMessage.userId()));
        } catch (Exception e) {
            log.error("Error parsing todo message", e);
            throw new RuntimeException(e);
        }
    }

}
