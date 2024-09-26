package ru.sbrf.file_loader.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.sbrf.file_loader.exception.DataValueException;
import ru.sbrf.file_loader.exception.DuplicateException;
import ru.sbrf.file_loader.exception.ErrorDetail;
import ru.sbrf.file_loader.exception.NoFoundException;
import ru.sbrf.file_loader.model.FileLink;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

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

    @ExceptionHandler(NoFoundException.class)
    public ResponseEntity<ErrorDetail> handleNoFoundException(NoFoundException e) {
        ErrorDetail errorDetail = new ErrorDetail("NoFoundException", e.getMessage(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetail);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorDetail> handleDataValueException(DuplicateException e) {
        // Формируем сообщение об ошибке, включая список дубликатов
        String duplicatesMessage = e.getDuplicates().stream()
                .map(FileLink::getFileLink) // Преобразуем каждый дубликат в строку (fileLink)
                .collect(Collectors.joining(", ", "Duplicate records: [", "]"));


        String errorMessage = e.getMessage() + ". " + duplicatesMessage;

        // Создаем объект ErrorDetail с новым сообщением
        ErrorDetail errorDetail = new ErrorDetail("DuplicateException", errorMessage, LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetail);
    }

}
