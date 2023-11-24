package us.dtaylor.todoservice.infastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import us.dtaylor.todoservice.domain.Todo;

import java.util.UUID;

@Document
public class TodoDocument {
    @Id
    private String id;

    private final String title;
    private final String description;
    private final boolean completed;
    private final String userId;

    public TodoDocument(Todo todo) {
        if (todo.getId() == null) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = todo.getId().toString();
        }
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.completed = todo.isCompleted();
        this.userId = todo.getUserId().toString();
    }

    public Todo toDomain() {
        return new Todo(
                UUID.fromString(id),
                title,
                description,
                completed,
                UUID.fromString(userId)
        );
    }
}
