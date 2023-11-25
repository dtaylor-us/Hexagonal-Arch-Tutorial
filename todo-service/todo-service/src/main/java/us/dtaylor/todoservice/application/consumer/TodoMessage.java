package us.dtaylor.todoservice.application.consumer;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.Objects;

@Jacksonized
@NoArgsConstructor
@Builder
public final class TodoMessage {
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private String userId;

    public TodoMessage(
            String id,
            String title,
            String description,
            boolean completed,
            String userId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.userId = userId;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public boolean completed() {
        return completed;
    }

    public String userId() {
        return userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (TodoMessage) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.title, that.title) &&
                Objects.equals(this.description, that.description) &&
                this.completed == that.completed &&
                Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, completed, userId);
    }

    @Override
    public String toString() {
        return "TodoMessage[" +
                "id=" + id + ", " +
                "title=" + title + ", " +
                "description=" + description + ", " +
                "completed=" + completed + ", " +
                "userId=" + userId + ']';
    }
}
