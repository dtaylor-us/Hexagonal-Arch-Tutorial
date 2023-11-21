package us.dtaylor.todoservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@Document
@EqualsAndHashCode
public class Todo {
    @Id
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private String userId;

    public Todo() {
    }
}
