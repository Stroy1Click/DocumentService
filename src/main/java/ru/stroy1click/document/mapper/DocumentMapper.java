package ru.stroy1click.document.mapper;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.stroy1click.common.mapper.Mappable;
import ru.stroy1click.document.dto.DocumentDto;
import ru.stroy1click.document.entity.Document;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentMapper implements Mappable<Document, DocumentDto> {

    private final ModelMapper modelMapper;

    @Override
    public Document toEntity(DocumentDto documentDto) {
        return this.modelMapper.map(documentDto, Document.class);
    }

    @Override
    public DocumentDto toDto(Document document) {
        return this.modelMapper.map(document, DocumentDto.class);
    }

    @Override
    public List<DocumentDto> toDto(List<Document> e) {
        return e.stream()
                .map(document -> this.modelMapper.map(document, DocumentDto.class))
                .toList();
    }

    public List<Document> toEntity(List<DocumentDto> e) {
        return e.stream()
                .map(documentDto -> this.modelMapper.map(documentDto, Document.class))
                .toList();
    }
}
