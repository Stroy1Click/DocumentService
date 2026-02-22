package ru.stroy1click.document.service;

import ru.stroy1click.common.event.OrderItemEvent;
import ru.stroy1click.document.dto.Item;

import java.util.List;

public interface PdfGeneratorService {

    byte[] generateSalesAgreement(List<OrderItemEvent> items);
}
