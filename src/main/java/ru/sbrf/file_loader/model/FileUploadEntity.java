package ru.sbrf.file_loader.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "file_upload")
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class FileUploadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "request_id")
    private String requestId;

    @NonNull
    @Column(name = "file_link")
    private String fileLink;

    @NonNull
    @Column(name = "status")
    private String status;

}