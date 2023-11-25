package us.dtaylor.todoservice.infastructure.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import us.dtaylor.todoservice.domain.Todo;

import java.util.UUID;

@Document
public class TodoDocument {
    @Id
    private String id;

    private String title;
    private String description;
    private boolean completed;
    private String userId;

    public static TodoDocument toDocument(Todo todo) {
        TodoDocument todoDocument = new TodoDocument();
        if (todo.getId() == null) {
            todoDocument.id = UUID.randomUUID().toString();
        } else {
            todoDocument.id = todo.getId().toString();
        }
        todoDocument.title = todo.getTitle();
        todoDocument.description = todo.getDescription();
        todoDocument.completed = todo.isCompleted();
        todoDocument.userId = todo.getUserId().toString();
        return todoDocument;
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
