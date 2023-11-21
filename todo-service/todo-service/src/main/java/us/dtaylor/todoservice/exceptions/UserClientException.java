package us.dtaylor.todoservice.exceptions;

public class UserClientException extends RuntimeException {
    public UserClientException(String message) {
        super(message);
    }
}
