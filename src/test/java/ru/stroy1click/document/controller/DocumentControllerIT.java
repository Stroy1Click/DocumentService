package ru.stroy1click.document.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import ru.stroy1click.common.dto.DocumentType;
import ru.stroy1click.document.config.TestcontainersConfiguration;
import ru.stroy1click.document.dto.DocumentDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import({TestcontainersConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocumentControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void get_WhenDocumentExistsAndValidIdProvided_ShouldReturnDocumentDto() {
        //Act
        ResponseEntity<DocumentDto> responseEntity = this.testRestTemplate
                .exchange("/api/v1/documents/1",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        DocumentDto.class);
        //Assert
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals(1L, responseEntity.getBody().getId());
        assertEquals(500L, responseEntity.getBody().getUserId());
        assertEquals("get-document-link.pdf", responseEntity.getBody().getLink());
        assertEquals(DocumentType.SALES_AGREEMENT, responseEntity.getBody().getType());
    }

    @Test
    void get_WhenDocumentDoesNotExist_ShouldReturnDocumentDto() {
        //Act
        ResponseEntity<ProblemDetail> responseEntity = this.testRestTemplate
                .exchange("/api/v1/documents/9990",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        ProblemDetail.class);

        //Assert
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("Не найдено", responseEntity.getBody().getTitle());
    }

    @Test
    void getByUserId_WhenDocumentsExistAndValidUserIdProvided_ShouldReturnDocumentDtos() {
        //Act
        ResponseEntity<List<DocumentDto>> responseEntity = this.testRestTemplate
                .exchange("/api/v1/documents?userId=400",
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<>() {});
        //Assert
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals(3, responseEntity.getBody().size());
    }

    @Test
    void delete_WhenDocumentExistsAndValidIdProvided_ShouldDeleteDocumentDto() {
        //Act
        ResponseEntity<String> responseEntity = this.testRestTemplate
                .exchange(
                        "/api/v1/documents/3",
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        String.class
                );

        //Assert
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("Документ удалён", responseEntity.getBody());

        ResponseEntity<ProblemDetail> notFoundResponseEntity = this.testRestTemplate
                .getForEntity("/api/v1/documents/3", ProblemDetail.class);

        assertTrue(notFoundResponseEntity.getStatusCode().is4xxClientError());
        Assertions.assertNotNull(notFoundResponseEntity.getBody());
        assertEquals("Не найдено", notFoundResponseEntity.getBody().getTitle());
    }

    @Test
    void delete_WhenDocumentDoesNotExist_ShouldThrowNotFoundException() {
        //Act
        ResponseEntity<ProblemDetail> responseEntity = this.testRestTemplate
                .exchange(
                        "/api/v1/documents/3",
                        HttpMethod.DELETE,
                        HttpEntity.EMPTY,
                        ProblemDetail.class
                );

        //Assert
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        Assertions.assertNotNull(responseEntity.getBody());
        assertEquals("Не найдено", responseEntity.getBody().getTitle());
    }
}
