package ru.sbrf.file_loader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.file_loader.model.FileStatusEnum;
import ru.sbrf.file_loader.model.FileUploadEntity;

import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileUploadEntity, Long> {
    List<FileUploadEntity> findByRequestId(String requestId);

    // Fetching FileUploadEntity by requestId and fileLink
    List<FileUploadEntity> findByRequestIdAndFileLink(String requestId, String fileLink);

    // Checking existence of a record by requestId, fileLink, and status
    boolean existsByRequestIdAndFileLinkAndStatus(String requestId, String fileLink, FileStatusEnum status);

    // Checking existence of a record by requestId and fileLink
    boolean existsByRequestIdAndFileLink(String requestId, String fileLink);

    // Fetching the latest status for each fileLink by requestId
    @Query("SELECT f FROM FileUploadEntity f WHERE f.requestId = :requestId AND f.timestamp = " +
            "(SELECT MAX(f2.timestamp) FROM FileUploadEntity f2 WHERE f2.fileLink = f.fileLink AND f2.requestId = f.requestId)")
    List<FileUploadEntity> findLatestStatusByRequestId(String requestId);

    // Fetching consumerName for a given requestId (assuming it's the same for all records under the same requestId)
    @Query("SELECT f.consumerName FROM FileUploadEntity f WHERE f.requestId = :requestId")
    String findConsumerByRequestId(@Param("requestId") String requestId);

    // Fetching distinct fileLinks for a given requestId
    // Fetching distinct fileLink strings for a given requestId
    @Query("SELECT DISTINCT f.fileLink FROM FileUploadEntity f WHERE f.requestId = :requestId")
    List<String> findFileLinksByRequestId(@Param("requestId") String requestId);


}