package us.dtaylor.todoservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}

