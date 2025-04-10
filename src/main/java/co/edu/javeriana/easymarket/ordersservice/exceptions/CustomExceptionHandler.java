package co.edu.javeriana.easymarket.ordersservice.exceptions;

import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.BadRequestException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.ResourceNotFoundException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.business_exceptions.UnauthorizedException;
import co.edu.javeriana.easymarket.ordersservice.exceptions.error_messages.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorMessage error = createErrorMessage(ex, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException ex) {
        ErrorMessage error = createErrorMessage(ex, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorMessage> handleUnauthorizedException(UnauthorizedException ex) {
        ErrorMessage error = createErrorMessage(ex, HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    private ErrorMessage createErrorMessage(Exception ex, HttpStatus status) {
        return new ErrorMessage(
                ex.getMessage(),
                Instant.now(),
                status.value(),
                status.getReasonPhrase()
        );
    }
}