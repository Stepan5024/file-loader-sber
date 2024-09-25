package ru.sbrf.file_loader.consumer;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;
import org.springframework.kafka.support.KafkaHeaders;

@Service
@AllArgsConstructor
public class FileUploadConsumer {

    private final FileUploadRepository fileUploadRepository;

    @KafkaListener(topics = "file_upload_topic", groupId = "file_upload_group")
    public void handleFileUpload(FileLink fileLink, @Header(KafkaHeaders.RECEIVED_KEY) String requestId) {
        // Обновление статуса на "in_progress"
        updateFileStatus(requestId, fileLink.getFileLink(), "in_progress");

        // Выполнение REST-запроса для загрузки файла (например, через RestTemplate)
        boolean success = uploadFile(fileLink);

        // Обновление статуса на "done" или "failed"
        updateFileStatus(requestId, fileLink.getFileLink(), success ? "done" : "failed");
    }

    private boolean uploadFile(FileLink fileLink) {
        // Здесь реализация REST-запроса для загрузки данных
        // Например, использование RestTemplate для отправки запроса
        return true; // Заглушка для успешной загрузки
    }

    private void updateFileStatus(String requestId, String fileLink, String status) {
        FileUploadEntity fileEntity = fileUploadRepository.findByRequestIdAndFileLink(requestId, fileLink);
        fileEntity.setStatus(status);
        fileUploadRepository.save(fileEntity);
    }
}
