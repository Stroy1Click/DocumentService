package ru.stroy1click.document.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.stroy1click.common.dto.DocumentType;


@Data
@Table(schema = "documents", name = "document")
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String link;

    @Enumerated(value = EnumType.STRING)
    private DocumentType type;

    private Long userId;
}
