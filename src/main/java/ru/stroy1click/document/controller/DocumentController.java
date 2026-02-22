package ru.stroy1click.document.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.stroy1click.document.dto.DocumentDto;
import ru.stroy1click.document.service.DocumentService;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    private final MessageSource messageSource;

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.documentService.get(id));
    }

    @GetMapping(params = "userId")
    public List<DocumentDto> getByUserId(@RequestParam("userId") Long userId) {
        return this.documentService.getByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteByLink(@PathVariable("id") Long id) {
        this.documentService.delete(id);

        return ResponseEntity.ok(this.messageSource.getMessage(
                "info.document.deleted",
                null,
                Locale.getDefault()
        ));
    }
}
