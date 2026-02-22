package ru.stroy1click.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.stroy1click.common.dto.DocumentType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {

    private Long id;

    private String link;

    private DocumentType type;

    private Long userId;
}
