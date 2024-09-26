package ru.sbrf.file_loader.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.sbrf.file_loader.exception.DataValueException;
import ru.sbrf.file_loader.exception.ErrorDetail;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> handleException(Exception e) {
        ErrorDetail errorDetail = new ErrorDetail(e.getClass().getSimpleName(), e.getMessage(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(errorDetail);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorDetail> handleIllegalStateException(IllegalStateException e) {
        ErrorDetail errorDetail = new ErrorDetail("IllegalStateException", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetail);
    }

    @ExceptionHandler(DataValueException.class)
    public ResponseEntity<ErrorDetail> handleDateValueValidationException(DataValueException e) {
        ErrorDetail errorDetail = new ErrorDetail("DateValueValidationException", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetail);
    }

}
