package ru.sbrf.file_loader.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.controller.response.FileStatusResponse;
import ru.sbrf.file_loader.model.*;
import ru.sbrf.file_loader.repository.FileUploadRepository;
import ru.sbrf.file_loader.util.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FileUploadService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final FileUploadRepository fileUploadRepository;

    // Логируем запрос в БД
    public void logUploadRequest(UploadRequest request) {
        log.info("Received upload request: {}", request);
        for (FileLink fileLink : request.getFileLinks()) {
            FileUploadEntity entity = new FileUploadEntity(request.getRequestId(), fileLink.getFileLink(), "pending");
            fileUploadRepository.save(entity);
            log.info("Saved file upload entity: {} with status 'pending'", fileLink.getFileLink());
        }
    }

    // Отправляем файлы на обработку
    public void processFileUploadRequest(UploadRequest request) {
        log.info("Processing file upload request for requestId: {}", request.getRequestId());

        for (FileLink fileLink : request.getFileLinks()) {
            String fileLinkJson = JsonUtil.toJson(fileLink);
            log.info("Sending file link {} to Kafka topic: {}", fileLinkJson, "file_upload_topic");
            kafkaTemplate.send("file_upload_topic", request.getRequestId(), fileLinkJson);
        }
    }

    // Получение статуса загрузки файлов
    public FileStatusResponse getStatus(String requestId) {
        log.info("Fetching status for requestId: {}", requestId);

        List<FileUploadEntity> files = fileUploadRepository.findByRequestId(requestId);
        List<FileStatus> fileStatuses = files.stream()
                .map(f -> new FileStatus(f.getFileLink(), f.getStatus()))
                .collect(Collectors.toList());

        log.info("Found {} files for requestId: {}", fileStatuses.size(), requestId);

        return new FileStatusResponse(requestId, fileStatuses);
    }
}
