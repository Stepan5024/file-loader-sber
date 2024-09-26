package ru.sbrf.file_loader.exception;

import lombok.Getter;
import ru.sbrf.file_loader.model.FileLink;

import java.util.List;


@Getter
public class DuplicateException extends RuntimeException {

    private final List<FileLink> duplicates;

    public DuplicateException(String message, List<FileLink> duplicates) {
        super(message);
        this.duplicates = duplicates;
    }

}
