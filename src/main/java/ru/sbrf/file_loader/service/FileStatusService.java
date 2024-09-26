package ru.sbrf.file_loader.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.exception.NoFoundException;
import ru.sbrf.file_loader.model.FileStatusEnum;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;

@Service
@Slf4j
@AllArgsConstructor
public class FileStatusService {

    private final FileUploadRepository fileUploadRepository;


    @Transactional
    public void updateFileStatus(String requestId, String fileLink, FileStatusEnum status) {
        String consumerName = fileUploadRepository.findConsumerByRequestId(requestId);

        if (consumerName == null) {
            log.error("No consumer found for requestId: {}", requestId);
            throw new NoFoundException("Consumer not found for requestId: " + requestId);
        }

        FileUploadEntity newEntity = new FileUploadEntity(requestId, fileLink, status, consumerName);
        fileUploadRepository.save(newEntity);
        log.info("Added new log entry for requestId: {}, fileLink: {}, status: {}, consumer: {}", requestId, fileLink,
                status, consumerName);
    }
}