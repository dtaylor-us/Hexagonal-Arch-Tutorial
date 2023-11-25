package us.dtaylor.userservice.infastructure;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import us.dtaylor.userservice.domain.User;

import java.util.UUID;

@Document
public class UserDocument {
    @Id
    private String id;

    private String name;
    private String email;

    public static UserDocument toDocument(User user) {
        UserDocument userDocument = new UserDocument();

        if (user.getId() == null) {
            userDocument.id = UUID.randomUUID().toString();
        } else {
            userDocument.id = user.getId().toString();
        }

        if (user.getId() == null) {
            userDocument.id = UUID.randomUUID().toString();
        } else {
            userDocument.id = user.getId().toString();
        }
        userDocument.name = user.getName();
        userDocument.email = user.getEmail();
        return userDocument;
    }

    public User toDomain() {
        return new User(
                UUID.fromString(id),
                name,
                email
        );
    }
}
