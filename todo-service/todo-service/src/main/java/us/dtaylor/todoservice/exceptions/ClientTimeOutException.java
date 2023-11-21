package us.dtaylor.todoservice.exceptions;

public class ClientTimeOutException extends ClientException {

    public ClientTimeOutException(String message) {
        super(message);
    }
}
