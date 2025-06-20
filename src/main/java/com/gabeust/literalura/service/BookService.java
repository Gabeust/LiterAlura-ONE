package com.gabeust.literalura.service;

import com.gabeust.literalura.ApiConsumer;
import com.gabeust.literalura.dto.BookDTO;
import com.gabeust.literalura.mapper.AuthorMapper;
import com.gabeust.literalura.mapper.BookMapper;
import com.gabeust.literalura.model.Book;
import com.gabeust.literalura.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final ApiConsumer apiConsumer;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, ApiConsumer apiConsumer, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.apiConsumer = apiConsumer;
        this.authorService = authorService;

    }

    public Optional<BookDTO> findByTitle(String title) throws InterruptedException, IOException {
        return apiConsumer.searchByTitle(title).stream()
                .filter(book -> book.title().equalsIgnoreCase(title))
                .findFirst()
                .map(book -> {
                    if (!bookRepository.existsById(book.id())) {
                        Book entity = BookMapper.toEntity(book);
                        bookRepository.save(entity);
                    }
                    return book;
                });
    }


    public List<BookDTO> findByLanguage(String lang) throws IOException, InterruptedException {
        return apiConsumer.searchByLanguage(lang);
    }

    public List<BookDTO> findTopDownloadedBooks(int quantity) throws IOException, InterruptedException {
        return apiConsumer.getTopDownloadedBooks(quantity);
    }

    @Transactional
    public List<BookDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toDTO)
                .toList();
    }

    public void saveAllIfNotExists(List<BookDTO> books) {
        books.forEach(bookDTO -> {
            if (!bookRepository.existsById(bookDTO.id())) {
                Book book = BookMapper.toEntity(bookDTO);
                // Controlamos autores con findOrCreate también
                book.setAuthors(book.getAuthors().stream()
                        .map(author -> authorService.findOrCreate(AuthorMapper.toDTO(author)))
                        .toList());
                bookRepository.save(book);
            }
        });
    }
}



