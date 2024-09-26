package ru.sbrf.file_loader.validate;

import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.model.FileLink;

import java.util.List;

public interface FileUploadValidator {
    List<FileLink> validate(UploadRequest request);
}