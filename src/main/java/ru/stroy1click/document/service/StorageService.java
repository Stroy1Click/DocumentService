package ru.stroy1click.document.service;

public interface StorageService {

    String uploadDocument(byte[] pdf);

    byte[] downloadDocument(String link);

    void deleteDocument(String link);
}
