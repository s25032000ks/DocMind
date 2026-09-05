package com.ai.docMind.exception;

import com.ai.docMind.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException exception){
        log.warn("Resource Not Found: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }

    @ExceptionHandler(DocumentProcessingException.class)
    public ResponseEntity<ApiResponse> handleDataProcessingException(DocumentProcessingException exception){
        log.warn("Failed to process Document: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(
                ApiResponse.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxUploadedSizeException(MaxUploadSizeExceededException exception){
        log.warn("File size limit exceed: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
                ApiResponse.builder()
                        .success(false)
                        .message("File size exceeds the allowed limit (25MB)")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();

        for(FieldError error: exception.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation Failed")
                        .data(errors)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalStateException(IllegalStateException exception){
        log.warn("Illegal State Exception: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenricException(Exception exception){
        log.warn("Unexpected Exception occurred: {}", exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .success(false)
                        .message(exception.getMessage())
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

    }
}
