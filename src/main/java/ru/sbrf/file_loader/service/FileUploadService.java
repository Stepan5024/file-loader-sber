package ru.sbrf.file_loader.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.controller.response.FileStatus;
import ru.sbrf.file_loader.controller.response.FileStatusResponse;
import ru.sbrf.file_loader.exception.DataValueException;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileUploadEntity;
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
            // Проверка дубликатов перед сохранением
            boolean duplicateExists = fileUploadRepository.existsByRequestIdAndFileLinkAndStatus(
                    request.getRequestId(),
                    fileLink.getFileLink(),
                    "pending"
            );

            if (duplicateExists) {
                log.error("Duplicate request found for requestId: {}, fileLink: {}, status: 'pending'",
                        request.getRequestId(), fileLink.getFileLink());
                throw new DataValueException("Duplicate record found for requestId, fileLink, and status 'pending'");
            }

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

        List<FileUploadEntity> latestFileStatuses = fileUploadRepository.findLatestStatusByRequestId(requestId);

        List<FileStatus> fileStatuses = latestFileStatuses.stream()
                .map(f -> new FileStatus(f.getFileLink(), f.getStatus()))
                .collect(Collectors.toList());

        log.info("Found {} latest files for requestId: {}", fileStatuses.size(), requestId);

        return new FileStatusResponse(requestId, fileStatuses);
    }
}
