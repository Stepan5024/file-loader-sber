package ru.sbrf.file_loader.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.loader.FileUploader;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileStatusEnum;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;
import ru.sbrf.file_loader.util.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FileUploadConsumer {

    private final FileUploadRepository fileUploadRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FileUploader fileUploader;

    @KafkaListener(topics = "file_upload_topic", groupId = "file_upload_group")
    public void handleFileUpload(String fileLinkJson, @Header(KafkaHeaders.RECEIVED_KEY) String requestId) {
        try {
            FileLink fileLink = JsonUtil.fromJson(fileLinkJson, FileLink.class);
            UploadRequest request = getUploadRequestByRequestId(requestId);

            assert fileLink != null;
            log.info("Received file link for processing: {}, requestId: {}", fileLink.getFileLink(), requestId);
            updateFileStatus(request, fileLink.getFileLink(), FileStatusEnum.IN_PROGRESS);

            boolean success = fileUploader.uploadFile(fileLink);
            FileStatusEnum status = success ? FileStatusEnum.DONE : FileStatusEnum.FAILED;
            updateFileStatus(request, fileLink.getFileLink(), status);

        } catch (IllegalStateException e) {
            log.error("Duplicate request for requestId: {} and fileLink: {}", requestId, e.getMessage(), e);
            // Отправка сообщения об ошибке
            sendErrorMessageToClient(requestId, "Duplicate record found.");
            throw e; // выбрасываем исключение дальше для Kafka
        } catch (Exception e) {
            log.error("Error processing file upload: {}", e.getMessage(), e);
        }
    }

    private UploadRequest getUploadRequestByRequestId(String requestId) {
        // Get the consumer name for the requestId
        String consumerName = fileUploadRepository.findConsumerByRequestId(requestId);

        // Get the list of fileLink strings for the requestId
        List<String> fileLinksStr = fileUploadRepository.findFileLinksByRequestId(requestId);

        // Map to FileLink objects (assuming FileLink has a constructor that accepts a String)
        List<FileLink> fileLinks = fileLinksStr.stream()
                .map(link -> {
                    FileLink fileLink = new FileLink();
                    fileLink.setFileLink(link); // Set the fileLink string manually
                    return fileLink;
                }) // Convert each string to a FileLink object
                .collect(Collectors.toList());

        // Return the UploadRequest object
        return new UploadRequest(requestId, consumerName, fileLinks);
    }

    private void sendErrorMessageToClient(String requestId, String errorMessage) {
        // Реализуйте логику отправки ошибки через Kafka или REST
        log.info("Sending error message to client for requestId {}: {}", requestId, errorMessage);
        // Например, отправка в другую тему Kafka для обратной связи
        kafkaTemplate.send("file_upload_error_topic", requestId, errorMessage);
    }

    private void updateFileStatus(UploadRequest request, String fileLink, FileStatusEnum status) {

        // Создание новой записи с новым статусом
        FileUploadEntity newEntity = new FileUploadEntity(request.getRequestId(), fileLink, status, request.getConsumer());
        fileUploadRepository.save(newEntity);
        log.info("Added new log entry for requestId: {}, fileLink: {}, status: {}", request.getRequestId(), fileLink, status);

    }

}
