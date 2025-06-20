package com.gabeust.literalura.service;


import com.gabeust.literalura.ApiConsumer;
import com.gabeust.literalura.dto.AuthorDTO;
import com.gabeust.literalura.dto.BookDTO;
import com.gabeust.literalura.mapper.AuthorMapper;
import com.gabeust.literalura.mapper.BookMapper;
import com.gabeust.literalura.model.Author;
import com.gabeust.literalura.model.Book;
import com.gabeust.literalura.repository.AuthorRepository;
import com.gabeust.literalura.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ApiConsumer apiConsumer;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository, ApiConsumer apiConsumer) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.apiConsumer = apiConsumer;
    }


    public List<AuthorDTO> findAll(){
       return authorRepository.findAll().stream()
               .map(AuthorMapper::toDTO)
               .toList();
    }

    public Author findOrCreate(AuthorDTO dto) {
        return authorRepository.findByNameAndBirthYearAndDeathYear(dto.name(), dto.birthYear(), dto.deathYear())
                .orElseGet(() -> authorRepository.save(AuthorMapper.toEntity(dto)));
    }


    public List<BookDTO> findByAuthor(String authorName) throws InterruptedException, IOException {
        List<BookDTO> books = apiConsumer.searchByAuthor(authorName);

        books.forEach(book -> {
            if (!bookRepository.existsById(book.id())) {
                Book entity = BookMapper.toEntity(book);
                // Antes de guardar el libro, aseguramos que autores no se dupliquen
                entity.setAuthors(entity.getAuthors().stream()
                        .map(author -> findOrCreate(AuthorMapper.toDTO(author)))
                        .toList());
                bookRepository.save(entity);
            }
        });

        return books;
    }

    public List<BookDTO> findBooksByAuthorsAliveBetween(int startYear, int endYear) throws IOException, InterruptedException {
        return apiConsumer.searchBooksByAuthorYearRange(startYear, endYear);
    }
}
