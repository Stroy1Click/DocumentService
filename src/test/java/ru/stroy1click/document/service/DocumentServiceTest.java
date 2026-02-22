package ru.stroy1click.document.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import ru.stroy1click.common.dto.DocumentType;
import ru.stroy1click.common.dto.Unit;
import ru.stroy1click.common.event.OrderCreatedEvent;
import ru.stroy1click.common.event.OrderItemEvent;
import ru.stroy1click.common.exception.NotFoundException;
import ru.stroy1click.document.dto.DocumentDto;
import ru.stroy1click.document.entity.Document;
import ru.stroy1click.document.mapper.DocumentMapper;
import ru.stroy1click.document.repository.DocumentRepository;
import ru.stroy1click.document.service.impl.DocumentServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @Mock
    private StorageService storageService;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DocumentServiceImpl documentService;

    private Document document;

    private DocumentDto documentDto;

    private Long userId;

    private Long id;

    private Long notExistId;

    private String link;

    @BeforeEach
    void setUp(){
        document = Document.builder()
                .id(1L)
                .link("link.pdf")
                .type(DocumentType.SALES_AGREEMENT)
                .userId(1L)
                .build();

        documentDto = DocumentDto.builder()
                .id(1L)
                .link("link.pdf")
                .type(DocumentType.SALES_AGREEMENT)
                .userId(1L)
                .build();

        userId = 1L;

        id = 1L;

        notExistId = 999L;

        link = "link.pdf";
    }

    @Test
    void getWhenDocumentExistsAndValidIdProvided_ShouldReturnDocumentDto() {
        //Arrange
        when(this.documentRepository.findById(id)).thenReturn(Optional.of(document));
        when(this.documentMapper.toDto(document)).thenReturn(documentDto);

        //Act
        DocumentDto foundDocument = this.documentService.get(id);

        //Assert
        assertNotNull(foundDocument.getId());
        assertEquals(document.getId(), foundDocument.getId());
        assertEquals(document.getType(), foundDocument.getType());
        assertEquals(document.getLink(), foundDocument.getLink());
        assertEquals(document.getUserId(), foundDocument.getUserId());
    }

    @Test
    void getByUserId_WhenDocumentsExist_ShouldReturnDocumentsList(){
        //Arrange
        when(this.documentRepository.findByUserId(userId)).thenReturn(List.of(document));
        when(this.documentMapper.toDto(List.of(document))).thenReturn(List.of(documentDto));

        //Act
        List<DocumentDto> foundDocumentsList = this.documentService.getByUserId(userId);

        //Assert
        assertFalse(foundDocumentsList.isEmpty());
        assertEquals(1, foundDocumentsList.size());
    }

    @Test
    void getByUserId_WhenDocumentsDoNotExist_ShouldReturnEmptyList(){
        //Arrange
        when(this.documentRepository.findByUserId(userId)).thenReturn(List.of());

        //Act
        List<DocumentDto> foundDocumentsList = this.documentService.getByUserId(userId);

        //Assert
        assertTrue(foundDocumentsList.isEmpty());
    }

    @Test
    void get_WhenDocumentDoesNotExist_ShouldThrowNotFoundException() {
        //Arrange
        when(this.documentRepository.findById(notExistId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(NotFoundException.class, () ->
                this.documentService.get(notExistId));
    }

    @Test
    void createSalesAgreement_WhenValidDataProvided_ShouldReturnCreatedDocument() {
        //Arrange
        OrderItemEvent firstItem = OrderItemEvent.builder()
                .id(1L)
                .productId(1)
                .productTitle("First Product Title")
                .price(BigDecimal.ONE)
                .quantity(100)
                .unit(Unit.LITER)
                .build();
        OrderItemEvent secondItem = OrderItemEvent.builder()
                .id(2L)
                .productId(2)
                .productTitle("Second Product Title")
                .price(BigDecimal.ONE)
                .quantity(500)
                .unit(Unit.PACK)
                .build();
        List<OrderItemEvent> orderItemEvents = List.of(firstItem, secondItem);
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderItems(orderItemEvents)
                .userId(100L)
                .build();
        when(this.pdfGeneratorService.generateSalesAgreement(orderItemEvents))
                .thenReturn(new byte[]{1,2,3,4,5});
        when(this.storageService.uploadDocument(any(byte[].class))).thenReturn(link);
        when(this.documentRepository.save(any(Document.class))).thenReturn(document);
        when(this.documentMapper.toDto(document)).thenReturn(documentDto);

        //Act
        DocumentDto createdDocument = this.documentService.createSalesAgreement(event);

        //Assert
        assertNotNull(createdDocument.getId());
        assertEquals(userId, createdDocument.getUserId());
        assertEquals(DocumentType.SALES_AGREEMENT, createdDocument.getType());
        assertEquals(link, createdDocument.getLink());
    }

    @Test
    void delete_WhenDocumentExistsAndValidLinkProvided_ShouldDeleteDocument() {
        //Arrange
        when(this.documentRepository.findById(id)).thenReturn(Optional.of(document));
        doNothing().when(this.documentRepository).delete(document);

        //Arrange
        this.documentService.delete(id);

        //Assert
        verify(this.documentRepository).findById(id);
        verify(this.documentRepository).delete(document);
    }

    @Test
    void delete_WhenDocumentDoesNotExists_ShouldThrowNotFoundException() {
        //Arrange
        when(this.documentRepository.findById(notExistId)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(NotFoundException.class, () ->
                this.documentService.delete(notExistId));
    }
}
