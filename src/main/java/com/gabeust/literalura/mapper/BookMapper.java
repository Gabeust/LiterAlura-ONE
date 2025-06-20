package com.gabeust.literalura.mapper;

import com.gabeust.literalura.dto.AuthorDTO;
import com.gabeust.literalura.dto.BookDTO;
import com.gabeust.literalura.model.Author;
import com.gabeust.literalura.model.Book;

import java.util.List;

public class BookMapper {

    public static Book toEntity(BookDTO dto) {
        if (dto == null) return null;

        Book entity = new Book();
        entity.setId(dto.id());
        entity.setTitle(dto.title());
        entity.setSummaries(dto.summaries());
        entity.setSubjects(dto.subjects());
        entity.setLanguages(dto.languages());
        entity.setDownloadCount(dto.download_count());

        if (dto.authors() != null) {
            List<Author> authorEntities = dto.authors().stream()
                    .map(AuthorMapper::toEntity)
                    .toList();
            entity.setAuthors(authorEntities);
        }

        return entity;
    }

    public static BookDTO toDTO(Book entity) {
        if (entity == null) return null;

        List<AuthorDTO> authorsDTO = null;
        if (entity.getAuthors() != null) {
            authorsDTO = entity.getAuthors().stream()
                    .map(AuthorMapper::toDTO)
                    .toList();
        }

        return new BookDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getSummaries(),
                authorsDTO,
                entity.getSubjects(),
                entity.getLanguages(),
                entity.getDownloadCount()
        );
    }
}
