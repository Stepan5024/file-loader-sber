package ru.sbrf.file_loader.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileProcessingMessageDto {
    private String requestId;
    private String consumerName;
    private String fileLink;
}
