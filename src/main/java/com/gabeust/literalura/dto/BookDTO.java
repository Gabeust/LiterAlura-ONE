package com.gabeust.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDTO(
        Long id,
        String title,
        List<String> summaries,
        List<AuthorDTO> authors,
        List<String> subjects,
        List<String> languages,
        int download_count
) {}