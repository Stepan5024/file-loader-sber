package ru.sbrf.file_loader.model;

import lombok.Data;

import java.util.List;

@Data
public class UploadRequest {
    private String requestId;
    private String consumer;
    private List<FileLink> fileLinks;

}