package ru.sbrf.file_loader.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.controller.response.FileStatus;
import ru.sbrf.file_loader.controller.response.FileStatusResponse;
import ru.sbrf.file_loader.exception.DuplicateException;
import ru.sbrf.file_loader.exception.NoFoundException;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;
import ru.sbrf.file_loader.util.JsonUtil;
import ru.sbrf.file_loader.validate.FileUploadValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FileUploadService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final FileUploadValidator fileUploadValidator;
    private final FileUploadProcessor fileUploadProcessor;
    private final FileUploadRepository fileUploadRepository;




    public void checkAndProcessUploadRequest(UploadRequest request) {
        log.info("Received upload request: {}", request);

        // Шаг 1: Валидация
        List<FileLink> duplicateLinks = fileUploadValidator.validate(request);

        // Шаг 2: Обработка корректных данных
        for (FileLink fileLink : request.getFileLinks()) {
            if (!duplicateLinks.contains(fileLink)) {
                fileUploadProcessor.process(request.getRequestId(), fileLink);
            }
        }

        // Шаг 3: Отправка в Kafka
        processFileUploadRequest(request);

        // Шаг 4: Если найдены дубликаты, выбросить исключение
        if (!duplicateLinks.isEmpty()) {
            log.error("Found {} duplicate records for requestId: {}", duplicateLinks.size(), request.getRequestId());
            throw new DuplicateException("Duplicate records found", duplicateLinks);
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

        if (latestFileStatuses.isEmpty()) {
            log.error("No records found for requestId: {}", requestId);
            throw new NoFoundException("No records found for requestId: " + requestId);
        }

        List<FileStatus> fileStatuses = latestFileStatuses.stream()
                .map(f -> new FileStatus(f.getFileLink(), f.getStatus()))
                .collect(Collectors.toList());

        log.info("Found {} latest files for requestId: {}", fileStatuses.size(), requestId);

        return new FileStatusResponse(requestId, fileStatuses);
    }
}
