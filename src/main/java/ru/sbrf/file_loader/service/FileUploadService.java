package ru.sbrf.file_loader.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.model.*;
import ru.sbrf.file_loader.repository.FileUploadRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FileUploadService {

    private final KafkaTemplate<String, FileLink> kafkaTemplate;

    private final FileUploadRepository fileUploadRepository;

    // Логируем запрос в БД
    public void logUploadRequest(UploadRequest request) {
        for (FileLink fileLink : request.getFileLinks()) {
            FileUploadEntity entity = new FileUploadEntity(request.getRequestId(), fileLink.getFileLink(), "pending");
            fileUploadRepository.save(entity);
        }
    }

    // Отправляем файлы на обработку
    public void processFileUploadRequest(UploadRequest request) {
        for (FileLink fileLink : request.getFileLinks()) {
            kafkaTemplate.send("file_upload_topic", request.getRequestId(), fileLink);
        }
    }

    // Получение статуса загрузки файлов
    public FileStatusResponse getStatus(String requestId) {
        List<FileUploadEntity> files = fileUploadRepository.findByRequestId(requestId);
        List<FileStatus> fileStatuses = files.stream()
                .map(f -> new FileStatus(f.getFileLink(), f.getStatus()))
                .collect(Collectors.toList());

        return new FileStatusResponse(requestId, fileStatuses);
    }
}
