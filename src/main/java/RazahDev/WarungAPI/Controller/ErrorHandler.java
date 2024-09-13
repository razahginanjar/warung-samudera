package RazahDev.WarungAPI.Controller;

import RazahDev.WarungAPI.DTO.GenericResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ResponseStatusException.class})
    public ResponseEntity<GenericResponse<?>> responseResponseEntity(ResponseStatusException exception)
    {
        return ResponseEntity.status(exception.getStatusCode())
                .body(
                        GenericResponse.builder()
                                .message(exception.getMessage())
                                .status(exception.getStatusCode().value())
                                .build()
                );
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<GenericResponse<?>> GenericResponseResponseEntity(ConstraintViolationException exception)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        GenericResponse.builder()
                                .message(exception.getMessage())
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public final ResponseEntity<GenericResponse<?>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(GenericResponse.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatusCode().value())
                        .build()
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<GenericResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GenericResponse.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public final ResponseEntity<GenericResponse<?>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(GenericResponse.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatusCode().value())
                        .build());
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<GenericResponse<?>> handleAuthException(AuthenticationException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<GenericResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(GenericResponse.builder()
                        .message(ex.getMessage())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build());
    }
}
