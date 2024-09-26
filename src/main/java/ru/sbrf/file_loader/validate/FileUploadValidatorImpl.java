package ru.sbrf.file_loader.validate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.model.FileLink;
import ru.sbrf.file_loader.model.FileStatusEnum;
import ru.sbrf.file_loader.model.FileUploadEntity;
import ru.sbrf.file_loader.repository.FileUploadRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FileUploadValidatorImpl implements FileUploadValidator {

    private final FileUploadRepository fileUploadRepository;

    public FileUploadValidatorImpl(FileUploadRepository fileUploadRepository) {
        this.fileUploadRepository = fileUploadRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FileLink> validate(UploadRequest request) {
        List<FileLink> duplicateLinks = new ArrayList<>();

        for (FileLink fileLink : request.getFileLinks()) {
            List<FileUploadEntity> existingEntries = fileUploadRepository.findByRequestIdAndFileLink(request.getRequestId(), fileLink.getFileLink());

            if (!existingEntries.isEmpty()) {
                FileUploadEntity lastEntry = existingEntries.stream()
                        .max(Comparator.comparing(FileUploadEntity::getTimestamp))
                        .orElse(null);

                if (lastEntry.getStatus() == FileStatusEnum.IN_PROGRESS || lastEntry.getStatus() == FileStatusEnum.PENDING) {
                    duplicateLinks.add(fileLink);
                }
            }
        }

        return duplicateLinks;
    }
}