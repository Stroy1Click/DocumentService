package ru.stroy1click.document.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.stroy1click.common.event.OrderCreatedEvent;
import ru.stroy1click.document.dto.DocumentDto;
import ru.stroy1click.document.service.DocumentService;
import ru.stroy1click.outbox.consumer.entity.ProcessedEvent;
import ru.stroy1click.outbox.consumer.service.ProcessedEventService;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = {"order-created-events"})
public class OrderCreatedEventsHandler {

    private final DocumentService documentService;

    private final ProcessedEventService processedEventService;

    @KafkaHandler
    public void handle(@Header(name = "messageId") byte[] messageIdBytes, @Payload OrderCreatedEvent event){
        log.info("Received {}", event);

        Long messageId = Long.valueOf(new String(messageIdBytes));

        if(this.processedEventService.findByMessageId(messageId).isPresent()){
            log.warn("Event with messageId {} already processed. Skipping.", messageId);
            return;
        }

        DocumentDto createdDocument =
                this.documentService.createSalesAgreement(event);

        this.processedEventService.save(new ProcessedEvent(null, messageId));

        log.info("created {}", createdDocument);
    }
}
