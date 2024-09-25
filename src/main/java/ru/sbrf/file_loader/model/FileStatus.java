package ru.sbrf.file_loader.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileStatus {
    private String fileLink;
    private String status;


}