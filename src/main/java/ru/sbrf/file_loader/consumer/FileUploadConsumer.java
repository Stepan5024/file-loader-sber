package ru.sbrf.file_loader.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.loader.FileUploader;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileProcessingMessageDto;
import ru.sbrf.file_loader.model.FileStatusEnum;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;
import ru.sbrf.file_loader.util.JsonUtil;

@Service
@AllArgsConstructor
@Slf4j
public class FileUploadConsumer {

    private final FileUploadRepository fileUploadRepository;
    private final FileUploader fileUploader;

    @KafkaListener(topics = "file_upload_topic", groupId = "file_upload_group", concurrency = "10")
    public void handleFileUpload(String messageJson, @Header(KafkaHeaders.RECEIVED_KEY) String requestId) {
        long threadId = Thread.currentThread().getId();
        log.info("Thread {} started handling file upload for messageJson: {}", threadId, messageJson);

        try {
            FileProcessingMessageDto message = JsonUtil.fromJson(messageJson, FileProcessingMessageDto.class);

            log.info("Thread {} is processing file link: {} for requestId: {}", threadId, message.getFileLink(), message.getRequestId());

            // Обновляем статус на IN_PROGRESS
            updateFileStatus(message, FileStatusEnum.IN_PROGRESS);

            // Выполняем загрузку файла
            boolean success = fileUploader.uploadFile(new FileLink(message.getFileLink()));

            // Обновляем статус в зависимости от результата
            FileStatusEnum status = success ? FileStatusEnum.DONE : FileStatusEnum.FAILED;
            updateFileStatus(message, status);

        } catch (Exception e) {
            log.error("Error processing file upload: {}", e.getMessage(), e);
            // Возможно, стоит добавить механизм повторных попыток или уведомления
        }
    }

    private void updateFileStatus(FileProcessingMessageDto message, FileStatusEnum status) {
        FileUploadEntity newEntity = new FileUploadEntity(
                message.getRequestId(),
                message.getFileLink(),
                status,
                message.getConsumerName()
        );
        fileUploadRepository.save(newEntity);
        log.info("Updated status for requestId: {}, fileLink: {}, status: {}",
                message.getRequestId(), message.getFileLink(), status);
    }
}
