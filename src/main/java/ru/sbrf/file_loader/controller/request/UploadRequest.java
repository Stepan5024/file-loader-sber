package ru.sbrf.file_loader.controller.request;

import lombok.Data;
import ru.sbrf.file_loader.model.FileLink;

import java.util.List;

@Data
public class UploadRequest {
    private String requestId;
    private String consumer;
    private List<FileLink> fileLinks;

}