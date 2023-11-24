package us.dtaylor.todoservice.domain.exceptions;

public class ClientTimeOutException extends ClientException {

    public ClientTimeOutException(String message) {
        super(message);
    }
}
