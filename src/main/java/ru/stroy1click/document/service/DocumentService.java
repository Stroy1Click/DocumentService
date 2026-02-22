package ru.stroy1click.document.service;

import ru.stroy1click.common.event.OrderCreatedEvent;
import ru.stroy1click.document.dto.DocumentDto;

import java.util.List;

public interface DocumentService {

    DocumentDto get(Long id);

    List<DocumentDto> getByUserId(Long userId);

    DocumentDto createSalesAgreement(OrderCreatedEvent event);

    void delete(Long id);
}
