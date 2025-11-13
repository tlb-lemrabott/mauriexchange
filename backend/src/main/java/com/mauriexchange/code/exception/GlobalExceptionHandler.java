package com.mauriexchange.code.exception;

import com.mauriexchange.code.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackages = "com.mauriexchange.code.controller")
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleDataNotFoundException(
            DataNotFoundException ex, WebRequest request) {
        log.error("Data not found: {}", ex.getMessage());
        
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(DataProcessingException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleDataProcessingException(
            DataProcessingException ex, WebRequest request) {
        log.error("Data processing error: {}", ex.getMessage());
        
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadRequestException(
            BadRequestException ex, WebRequest request) {
        log.error("Bad request: {}", ex.getMessage());
        
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(false)
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .success(false)
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
