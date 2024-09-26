package ru.sbrf.file_loader.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.sbrf.file_loader.model.FileLink;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Запрос на загрузку списка файлов")
@Component
public class UploadRequest {

    @Schema(description = "Уникальный идентификатор запроса загрузки")
    @NotNull(message = "Уникальный идентификатор должен быть заполнен")
    String requestId;

    @Schema(description = "Название клиента")
    @NotBlank(message = "Клиент должен иметь название")
    String consumer;

    @Schema(description = "Список ссылок для загрузки")
    @NotEmpty(message = "Список ссылок для загрузки должен содержать хотя бы одну ссылку")
    @Size(min = 1, message = "Список ссылок должен содержать хотя бы одну ссылку")
    List<FileLink> fileLinks;

}