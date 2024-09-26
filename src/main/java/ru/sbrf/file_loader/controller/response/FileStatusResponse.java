package ru.sbrf.file_loader.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
public class FileStatusResponse {

    @Schema(description = "Уникальный идентификатор запроса загрузки")
    @NotNull(message = "Уникальный идентификатор должен быть заполнен")
    String requestId;

    @Schema(description = "Список статусов загрузки")
    List<FileStatus> fileLinks;

}