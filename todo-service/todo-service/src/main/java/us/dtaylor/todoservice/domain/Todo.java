package us.dtaylor.todoservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Document
public class Todo {
    @Id
    private String id;
    @Setter
    private String title;
    @Setter
    private String description;
    @Setter
    private boolean completed;

}
