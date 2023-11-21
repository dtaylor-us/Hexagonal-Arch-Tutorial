package us.dtaylor.todoservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientException.class)
    public Mono<ResponseEntity<String>> handleClientException(ClientException ex, ServerWebExchange exchange) {
        // Log the exception details
        // Return a response entity with appropriate HTTP status and message
        log.error("Client exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage()));
    }

    @ExceptionHandler(ClientTimeOutException.class)
    public Mono<ResponseEntity<String>> handleClientTimeOutException(ClientTimeOutException ex, ServerWebExchange exchange) {
        // Log the exception details
        // Return a response entity with appropriate HTTP status and message
        log.error("Client timeout exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<String>> handleUserNotFoundException(UserNotFoundException ex, ServerWebExchange exchange) {
        // Log the exception details
        // Return a response entity with appropriate HTTP status and message
        log.error("User not found exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage()));
    }

    // Generic exception handler as a fallback
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        // Log the exception details
        // Return a generic response entity
        log.error("Generic exception: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred"));
    }
}
