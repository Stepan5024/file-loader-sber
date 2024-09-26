package ru.sbrf.file_loader.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sbrf.file_loader.model.FileStatusEnum;

@Data
@AllArgsConstructor
public class FileStatus {
    private String fileLink;
    private FileStatusEnum status;

}