package ru.sbrf.file_loader.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FileStatusResponse {
    private String requestId;
    private List<FileStatus> fileLinks;

}