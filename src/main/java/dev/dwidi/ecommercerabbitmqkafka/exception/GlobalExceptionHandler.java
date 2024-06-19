package dev.dwidi.ecommercerabbitmqkafka.exception;


import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.function.Supplier;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Autowired
    public Supplier<String> uuidSupplier;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PublicResponseDTO<String>> handleGlobalException(Exception e, WebRequest request) {
        String path = request.getDescription(false);
        log.error("An error occurred while processing a request: {}", path, e);

        // Generate requestId
        String requestID = uuidSupplier.get();

        PublicResponseDTO<String>  publicResponseDTO = new PublicResponseDTO<>(
                requestID, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),null
        );

        return new ResponseEntity<>(publicResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<PublicResponseDTO<String>> handleUserNotFoundException(UserNotFoundException e, WebRequest request) {
        String path = request.getDescription(false);
        log.error("User not found: {}", path, e);

        // Generate requestId
        String requestID = uuidSupplier.get();

        PublicResponseDTO<String> publicResponseDTO = new PublicResponseDTO<>(
                requestID, HttpStatus.NOT_FOUND.value(), e.getMessage(), null
        );

        return new ResponseEntity<>(publicResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<PublicResponseDTO<String>> handleProductNotFoundException(ProductNotFoundException e, WebRequest request) {
        String path = request.getDescription(false);
        log.error("Product not found: {}", path, e);

        // Generate requestId
        String requestId = uuidSupplier.get();

        PublicResponseDTO<String> publicResponseDTO = new PublicResponseDTO<>(
                requestId, HttpStatus.NOT_FOUND.value(), e.getMessage(), null
        );

        return new ResponseEntity<>(publicResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<PublicResponseDTO<String>> handleTransactionNotFound(TransactionNotFoundException e, WebRequest request) {
        String path = request.getDescription(false);
        log.error("Transaction not found: {}", path, e);

        // Generate requestId
        String requestId = uuidSupplier.get();

        PublicResponseDTO<String> publicResponseDTO = new PublicResponseDTO<>(
                requestId, HttpStatus.NOT_FOUND.value(), e.getMessage(), null);

        return new ResponseEntity<>(publicResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<PublicResponseDTO<String>> handleTransactionNotFound(UsernameAlreadyExistsException e, WebRequest request) {
        String path = request.getDescription(false);
        log.error("Username already exist: {}", path, e);

        // Generate requestId
        String requestId = uuidSupplier.get();

        PublicResponseDTO<String> publicResponseDTO = new PublicResponseDTO<>(
                requestId, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);

        return new ResponseEntity<>(publicResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<PublicResponseDTO<String>> handleTransactionNotFound(EmailAlreadyExistsException e, WebRequest request) {
        String path = request.getDescription(false);
        log.error("Username already exist: {}", path, e);

        // Generate requestId
        String requestId = uuidSupplier.get();

        PublicResponseDTO<String> publicResponseDTO = new PublicResponseDTO<>(
                requestId, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);

        return new ResponseEntity<>(publicResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<PublicResponseDTO<String>> handleInvalidCredentialsException(InvalidCredentialsException e, WebRequest request) {
        String path = request.getDescription(false);
        log.error("Invalid credentials: {}", path, e);

        // Generate requestId
        String requestId = uuidSupplier.get();

        PublicResponseDTO<String> publicResponseDTO = new PublicResponseDTO<>(
                requestId, HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null);

        return new ResponseEntity<>(publicResponseDTO, HttpStatus.UNAUTHORIZED);
    }
}
