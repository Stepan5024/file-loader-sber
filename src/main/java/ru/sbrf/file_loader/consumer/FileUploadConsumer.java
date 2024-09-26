package ru.sbrf.file_loader.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final FileUploader fileUploader;

    @Transactional
    @KafkaListener(topics = "file_upload_topic", groupId = "file_upload_group", concurrency = "10")
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
            throw e;
        } catch (Exception e) {
            log.error("Error processing file upload: {}", e.getMessage(), e);
        }
    }

    private UploadRequest getUploadRequestByRequestId(String requestId) {
        String consumerName = fileUploadRepository.findConsumerByRequestId(requestId);

        List<String> fileLinksStr = fileUploadRepository.findFileLinksByRequestId(requestId);

        List<FileLink> fileLinks = fileLinksStr.stream()
                .map(link -> {
                    FileLink fileLink = new FileLink();
                    fileLink.setFileLink(link);
                    return fileLink;
                })
                .collect(Collectors.toList());
        return new UploadRequest(requestId, consumerName, fileLinks);
    }

    private void updateFileStatus(UploadRequest request, String fileLink, FileStatusEnum status) {
        FileUploadEntity newEntity = new FileUploadEntity(request.getRequestId(), fileLink, status, request.getConsumer());
        fileUploadRepository.save(newEntity);
        log.info("Added new log entry for requestId: {}, fileLink: {}, status: {}", request.getRequestId(), fileLink, status);

    }

}
