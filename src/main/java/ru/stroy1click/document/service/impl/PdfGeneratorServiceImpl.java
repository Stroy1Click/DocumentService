package ru.stroy1click.document.service.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import ru.stroy1click.common.event.OrderItemEvent;
import ru.stroy1click.document.service.PdfGeneratorService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private final Configuration configuration;

    public byte[] generateSalesAgreement(List<OrderItemEvent> items) {
        try {
            Map<String, Object> model = new HashMap<>();

            model.put("date",
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            model.put("items", items);

            Template template = this.configuration.getTemplate("sales-agreement.ftlh");
            String html = FreeMarkerTemplateUtils
                    .processTemplateIntoString(template, model);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);

            builder.useFont(
                    () -> getFont("/fonts/DejaVuSans.ttf"),
                    "DejaVu Sans"
            );

            builder.toStream(out);
            builder.run();

            return out.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate contract PDF ", e);
            throw new RuntimeException("Failed to generate contract PDF", e);
        }
    }

    private InputStream getFont(String path) {
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            log.error("Font not found: {}", path);
            throw new IllegalStateException("Font not found: " + path);
        }
        return inputStream;
    }
}
