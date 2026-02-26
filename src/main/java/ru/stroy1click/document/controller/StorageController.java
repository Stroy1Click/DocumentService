package ru.stroy1click.document.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.stroy1click.document.service.StorageService;

@RestController
@RequestMapping("/api/v1/storage")
@Tag(name = "Storage Controller", description = "Работа с S3")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @GetMapping("/{fileName}")
    @Operation(summary = "Скачать документ")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        byte[] data = this.storageService.downloadDocument(fileName);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(data);
    }
}
