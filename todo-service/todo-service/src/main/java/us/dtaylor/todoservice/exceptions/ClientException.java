package us.dtaylor.todoservice.exceptions;

public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }
}
