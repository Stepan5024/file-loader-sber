package ru.sbrf.file_loader.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;
import org.springframework.kafka.support.KafkaHeaders;
import ru.sbrf.file_loader.util.JsonUtil;

@Service
@AllArgsConstructor
@Slf4j
public class FileUploadConsumer {

    private final FileUploadRepository fileUploadRepository;

    @KafkaListener(topics = "file_upload_topic", groupId = "file_upload_group")
    public void handleFileUpload(String fileLinkJson, @Header(KafkaHeaders.RECEIVED_KEY) String requestId) {

        FileLink fileLink = JsonUtil.fromJson(fileLinkJson, FileLink.class);

        assert fileLink != null;
        log.info("Received file link for processing: {}, requestId: {}", fileLink.getFileLink(), requestId);
        // Обновление статуса на "in_progress"
        log.info("Updating status of file {} to 'in_progress'", fileLink.getFileLink());

        updateFileStatus(requestId, fileLink.getFileLink(), "in_progress");

        // Выполнение REST-запроса для загрузки файла (например, через RestTemplate)
        boolean success = uploadFile(fileLink);
        log.info("File upload {} for file {}: {}", success ? "successful" : "failed", fileLink.getFileLink(), success);

        String status = success ? "done" : "failed";
        log.info("Updating status of file {} to '{}'", fileLink.getFileLink(), status);
        updateFileStatus(requestId, fileLink.getFileLink(), status);
    }

    private boolean uploadFile(FileLink fileLink) {
        // Здесь реализация REST-запроса для загрузки данных
        log.info("Uploading file: {}", fileLink.getFileLink());
        // Например, использование RestTemplate для отправки запроса
        boolean success = true;  // Заглушка для успешной загрузки
        log.info("Upload result for file {}: {}", fileLink.getFileLink(), success ? "success" : "failure");
        return success;
    }

    private void updateFileStatus(String requestId, String fileLink, String status) {
        FileUploadEntity fileEntity = fileUploadRepository.findByRequestIdAndFileLink(requestId, fileLink);
        fileEntity.setStatus(status);
        fileUploadRepository.save(fileEntity);
        log.info("Updated file status in DB: requestId: {}, fileLink: {}, newStatus: {}", requestId, fileLink, status);

    }
}
