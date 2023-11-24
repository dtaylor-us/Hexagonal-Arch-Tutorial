package us.dtaylor.userservice.infastructure;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import us.dtaylor.userservice.domain.User;

import java.util.UUID;

@Document
public class UserDocument {
    @Id
    private String id;

    private final String name;
    private final String email;

    public UserDocument(User user) {
        if (user.getId() == null) {
            this.id = UUID.randomUUID().toString();
        } else {
            this.id = user.getId().toString();
        }
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public User toDomain() {
        return new User(
                UUID.fromString(id),
                name,
                email
        );
    }
}
