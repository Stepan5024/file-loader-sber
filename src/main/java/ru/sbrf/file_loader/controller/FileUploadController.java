package ru.sbrf.file_loader.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sbrf.file_loader.model.FileStatusResponse;
import ru.sbrf.file_loader.model.UploadRequest;
import ru.sbrf.file_loader.service.FileUploadService;

@RestController
@RequestMapping("/file")
@Slf4j
@AllArgsConstructor
public class FileUploadController {

    FileUploadService fileUploadService;

    @PostMapping("/send")
    public ResponseEntity<?> sendFiles(@RequestBody UploadRequest request) {
        // Логируем запрос в БД
        fileUploadService.logUploadRequest(request);

        // Отправляем каждый fileLink в Kafka для обработки
        fileUploadService.processFileUploadRequest(request);

        return ResponseEntity.ok("Files are being processed");
    }

    @GetMapping("/get")
    public ResponseEntity<?> getFileStatus(@RequestParam String requestId) {
        FileStatusResponse response = fileUploadService.getStatus(requestId);
        return ResponseEntity.ok(response);
    }
}
