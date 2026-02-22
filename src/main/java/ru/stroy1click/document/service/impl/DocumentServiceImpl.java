package ru.stroy1click.document.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.stroy1click.common.dto.DocumentType;
import ru.stroy1click.common.event.OrderCreatedEvent;
import ru.stroy1click.common.exception.NotFoundException;
import ru.stroy1click.document.dto.DocumentDto;
import ru.stroy1click.document.entity.Document;
import ru.stroy1click.document.mapper.DocumentMapper;
import ru.stroy1click.document.repository.DocumentRepository;
import ru.stroy1click.document.service.DocumentService;
import ru.stroy1click.document.service.PdfGeneratorService;
import ru.stroy1click.document.service.StorageService;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private final PdfGeneratorService pdfGeneratorService;

    private final StorageService storageService;

    private final DocumentMapper documentMapper;

    private final MessageSource messageSource;

    @Override
    @Cacheable(value = "document", key = "#id")
    public DocumentDto get(Long id) {
        return this.documentMapper.toDto(this.documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        this.messageSource.getMessage(
                                "error.document.not_found",
                                null,
                                Locale.getDefault()
                        )
                )));
    }

    @Override
    @Cacheable(value = "documentsByUserId", key = "#userId")
    public List<DocumentDto> getByUserId(Long userId) {
        log.info("getAllByUserId {}", userId);

        return this.documentMapper.toDto(
                this.documentRepository.findByUserId(userId)
        );
    }

    @Override
    @CacheEvict(value = "documentsByUserId", key = "#event.userId")
    public DocumentDto createSalesAgreement(OrderCreatedEvent event) {
        log.info("createSalesAgreement {}", event);

        byte[] pdf = this.pdfGeneratorService.generateSalesAgreement(event.getOrderItems());
        String fileName = this.storageService.uploadDocument(pdf);

        Document createdDocument = this.documentRepository.save(new Document(
                null, fileName, DocumentType.SALES_AGREEMENT, event.getUserId()
        ));

        return this.documentMapper.toDto(createdDocument);
    }

    @Override
    @CacheEvict(value = "document", key = "#id")
    public void delete(Long id) {
        log.info("deleteByLink {}", id);

        Document foundDocument = this.documentRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        this.messageSource.getMessage(
                                "error.document.not_found",
                                null,
                                Locale.getDefault()
                        )
                )
        );

        this.documentRepository.delete(foundDocument);
    }
}
