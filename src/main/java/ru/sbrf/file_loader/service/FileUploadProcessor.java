package ru.sbrf.file_loader.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileStatusEnum;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;

@Service
@Slf4j
@AllArgsConstructor
public class FileUploadProcessor {
    FileUploadRepository fileUploadRepository;

    // Логируем запрос в БД
    public void process(UploadRequest request, FileLink fileLink) {
        FileUploadEntity entity = new FileUploadEntity(request.getRequestId(),
                fileLink.getFileLink(),
                FileStatusEnum.PENDING,
                request.getConsumer());
        fileUploadRepository.save(entity);
        log.info("Saved file upload entity: {} with status 'pending'", fileLink.getFileLink());
    }
}