package ru.sbrf.file_loader.loader;

import ru.sbrf.file_loader.model.FileLink;

public interface FileUploader {
    boolean uploadFile(FileLink fileLink);
}