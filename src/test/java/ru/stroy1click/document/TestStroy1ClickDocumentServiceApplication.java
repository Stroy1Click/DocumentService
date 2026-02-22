package ru.stroy1click.document;

import org.springframework.boot.SpringApplication;
import ru.stroy1click.document.config.TestcontainersConfiguration;

public class TestStroy1ClickDocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(Stroy1ClickDocumentServiceApplication::main)
                .with(TestcontainersConfiguration.class).run(args);
    }

}
