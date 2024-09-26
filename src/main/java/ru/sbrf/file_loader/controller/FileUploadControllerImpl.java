package ru.sbrf.file_loader.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sbrf.file_loader.aspect.annotation.Loggable;
import ru.sbrf.file_loader.controller.request.StatusRequest;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.controller.response.FileStatusResponse;
import ru.sbrf.file_loader.service.FileUploadService;

@RestController("Загрузка и статус файлов")
@Slf4j
@RequiredArgsConstructor
public class FileUploadControllerImpl implements FileUploadController {

    private final FileUploadService fileUploadService;

    @Loggable
    @Override
    public ResponseEntity<?> sendFiles(UploadRequest request) {
        fileUploadService.checkAndProcessUploadRequest(request);
        return ResponseEntity.ok("Files are being processed");
    }

    @Loggable
    @Override
    public ResponseEntity<FileStatusResponse> getFileStatus(@RequestBody StatusRequest request) {
        FileStatusResponse response = fileUploadService.getStatus(request.getRequestId());
        return ResponseEntity.ok(response);
    }
}
