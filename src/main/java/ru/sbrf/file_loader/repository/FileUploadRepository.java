package ru.sbrf.file_loader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sbrf.file_loader.model.FileUploadEntity;

import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileUploadEntity, Long> {
    List<FileUploadEntity> findByRequestId(String requestId);
    FileUploadEntity findByRequestIdAndFileLink(String requestId, String fileLink);
}