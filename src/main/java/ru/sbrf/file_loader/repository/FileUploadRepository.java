package ru.sbrf.file_loader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.file_loader.model.FileUploadEntity;

import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileUploadEntity, Long> {
    
    List<FileUploadEntity> findByRequestIdAndFileLink(String requestId, String fileLink);

    
    @Query("SELECT f FROM FileUploadEntity f WHERE f.requestId = :requestId AND f.timestamp = " +
            "(SELECT MAX(f2.timestamp) FROM FileUploadEntity f2 WHERE f2.fileLink = f.fileLink AND f2.requestId = f.requestId)")
    List<FileUploadEntity> findLatestStatusByRequestId(String requestId);

    @Query("SELECT f.consumerName FROM FileUploadEntity f WHERE f.requestId = :requestId")
    String findConsumerByRequestId(@Param("requestId") String requestId);

    @Query("SELECT DISTINCT f.fileLink FROM FileUploadEntity f WHERE f.requestId = :requestId")
    List<String> findFileLinksByRequestId(@Param("requestId") String requestId);


}