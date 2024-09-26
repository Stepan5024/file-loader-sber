package ru.sbrf.file_loader.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.loader.FileUploader;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileStatusEnum;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;
import ru.sbrf.file_loader.util.JsonUtil;

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

            assert fileLink != null;
            log.info("Received file link for processing: {}, requestId: {}", fileLink.getFileLink(), requestId);
            updateFileStatus(requestId, fileLink.getFileLink(), FileStatusEnum.IN_PROGRESS);

            boolean success = fileUploader.uploadFile(fileLink);
            FileStatusEnum status = success ? FileStatusEnum.DONE :FileStatusEnum.FAILED;
            updateFileStatus(requestId, fileLink.getFileLink(), status);

        } catch (IllegalStateException e) {
            log.error("Duplicate request for requestId: {} and fileLink: {}", requestId, e.getMessage(), e);
            // Отправка сообщения об ошибке
            sendErrorMessageToClient(requestId, "Duplicate record found.");
            throw e; // выбрасываем исключение дальше для Kafka
        } catch (Exception e) {
            log.error("Error processing file upload: {}", e.getMessage(), e);
        }
    }

    private void sendErrorMessageToClient(String requestId, String errorMessage) {
        // Реализуйте логику отправки ошибки через Kafka или REST
        log.info("Sending error message to client for requestId {}: {}", requestId, errorMessage);
        // Например, отправка в другую тему Kafka для обратной связи
        kafkaTemplate.send("file_upload_error_topic", requestId, errorMessage);
    }

    private void updateFileStatus(String requestId, String fileLink, FileStatusEnum status) {

        // Создание новой записи с новым статусом
        FileUploadEntity newEntity = new FileUploadEntity(requestId, fileLink, status);
        fileUploadRepository.save(newEntity);
        log.info("Added new log entry for requestId: {}, fileLink: {}, status: {}", requestId, fileLink, status);

    }

}
