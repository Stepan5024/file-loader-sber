package ru.sbrf.file_loader.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sbrf.file_loader.controller.request.UploadRequest;
import ru.sbrf.file_loader.controller.response.FileStatus;
import ru.sbrf.file_loader.controller.request.StatusRequest;
import ru.sbrf.file_loader.controller.response.FileStatusResponse;

@Tag(name = "Сервис загрузки файлов по ссылке")
@RequestMapping("/api/v1/fileloader/file")
public interface FileUploadController {

    @Operation(summary = "Запрос на загрузку неких файлов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files are being processed",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content)
    })
    @PostMapping("/send")
    ResponseEntity<?> sendFiles(@Valid @RequestBody UploadRequest request);

    @Operation(summary = "Получить статус загрузки файлов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FileStatus.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content)
    })

    @GetMapping("/get")
    ResponseEntity<FileStatusResponse> getFileStatus(@Valid @RequestBody StatusRequest request);

}
