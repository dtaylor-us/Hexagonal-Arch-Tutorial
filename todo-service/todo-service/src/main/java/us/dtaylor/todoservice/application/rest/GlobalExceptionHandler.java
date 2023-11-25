package us.dtaylor.todoservice.application.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import us.dtaylor.todoservice.domain.exceptions.ClientException;
import us.dtaylor.todoservice.domain.exceptions.ClientTimeOutException;
import us.dtaylor.todoservice.domain.exceptions.UserNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleClientException(ClientException ex, ServerWebExchange exchange) {
        log.error("Client exception: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return Mono.just(ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage())));
    }

    @ExceptionHandler(ClientTimeOutException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleClientTimeOutException(ClientTimeOutException ex, ServerWebExchange exchange) {
        log.error("Client timeout exception: {}", ex.getMessage());
        HttpStatus gatewayTimeout = HttpStatus.GATEWAY_TIMEOUT;
        return Mono.just(ResponseEntity.status(gatewayTimeout).body(new ErrorResponse(gatewayTimeout.value(), gatewayTimeout.getReasonPhrase(), ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        log.error("Generic exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage())));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFoundException(UserNotFoundException ex, ServerWebExchange exchange) {
        log.error("Not found exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage())));
    }


    public record ErrorResponse(int statusCode, String reason, String message) {
    }
}
