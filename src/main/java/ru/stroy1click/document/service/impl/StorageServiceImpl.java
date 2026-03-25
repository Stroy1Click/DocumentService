package ru.stroy1click.document.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.stroy1click.common.exception.NotFoundException;
import ru.stroy1click.common.exception.StorageException;
import ru.stroy1click.document.prop.StorageProperties;
import ru.stroy1click.document.service.StorageService;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Locale;

@Slf4j
@Service
@AllArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final S3Client s3Client;

    private final StorageProperties storageProperties;

    private final MessageSource messageSource;

    @Override
    public String uploadDocument(byte[] pdf) {
        log.info("uploadDocument");
        String fileName = System.currentTimeMillis() + "_" + "document.pdf";
        try {
            this.s3Client.putObject(PutObjectRequest.builder()
                            .bucket(this.storageProperties.getBucketName())
                            .key(fileName)
                            .build(),
                    RequestBody.fromBytes(pdf));
        } catch (S3Exception e) {
            log.error("S3 Service Error: [Code: {}] {}", e.awsErrorDetails().errorCode(), e.getMessage());
            throw new StorageException(e);
        }  catch (Exception e) {
            log.error("Unexpected error during document upload to S3", e);
            throw new StorageException(e);
        }
        return fileName;
    }

    @Override
    @Cacheable(value = "documentFile", key = "#link")
    public byte[] downloadDocument(String link) {
        log.info("downloadDocument {}", link);
        try {
            ResponseBytes<GetObjectResponse> objectAsBytes =
                    this.s3Client.getObjectAsBytes(GetObjectRequest.builder()
                            .bucket(this.storageProperties.getBucketName())
                            .key(link)
                            .build());

            return objectAsBytes.asByteArray();
        } catch (NoSuchKeyException e){
            throw new NotFoundException(
                    this.messageSource.getMessage("error.storage.not_found",
                            new Object[]{link},
                            Locale.getDefault())
            );
        } catch (S3Exception e) {
            log.error("S3 Service Error: [Code: {}] {}", e.awsErrorDetails().errorCode(), e.getMessage());
            throw new StorageException(e);
        } catch (Exception e) {
            log.error("Unexpected error during document upload to S3", e);
            throw new StorageException(e);
        }
    }

    @Override
    @CacheEvict(value = "documentFile", key = "#link")
    public void deleteDocument(String link) {
        log.info("deleteDocument {}", link);
        try {
            this.s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(this.storageProperties.getBucketName())
                    .key(link)
                    .build());
        } catch (S3Exception e) {
            log.error("S3 Service Error: [Code: {}] {}", e.awsErrorDetails().errorCode(), e.getMessage());
            throw new StorageException(e);
        } catch (Exception e) {
            log.error("Unexpected error during document upload to S3", e);
            throw new StorageException(e);
        }
    }
}
