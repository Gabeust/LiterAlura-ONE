package com.gabeust.literalura.mapper;

import com.gabeust.literalura.dto.AuthorDTO;
import com.gabeust.literalura.model.Author;

public class AuthorMapper {
    public static Author toEntity(AuthorDTO dto) {
        if (dto == null) return null;

        Author entity = new Author();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setBirthYear(dto.birthYear());
        entity.setDeathYear(dto.deathYear());

        return entity;
    }

    public static AuthorDTO toDTO(Author entity) {
        if (entity == null) return null;

        return new AuthorDTO(
                entity.getId(),
                entity.getName(),
                entity.getBirthYear(),
                entity.getDeathYear()
        );
    }
}
