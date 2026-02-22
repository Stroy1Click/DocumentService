package ru.stroy1click.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "ru.stroy1click.document.entity",
        "ru.stroy1click.outbox.consumer.entity"
})
@EnableJpaRepositories(basePackages = {
        "ru.stroy1click.document.repository",
        "ru.stroy1click.outbox.consumer.repository"
})
public class Stroy1ClickDocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(Stroy1ClickDocumentServiceApplication.class, args);
    }

}