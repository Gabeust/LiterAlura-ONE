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
/**
 * Servicio encargado de gestionar la lógica relacionada con los libros,
 * incluyendo búsqueda, persistencia y sincronización con APIs externas.
 */
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
    /**
     * Busca un libro por título utilizando una API externa.
     * Si el libro no existe en la base de datos local, lo guarda.
     *
     * @param title título exacto del libro a buscar
     * @return un Optional con el BookDTO encontrado o vacío si no existe
     * @throws InterruptedException si la llamada a la API es interrumpida
     * @throws IOException si ocurre un error de entrada/salida en la llamada a la API
     */
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

    /**
     * Busca libros que estén en un idioma específico.
     *
     * @param lang código de idioma (ej. "en", "es")
     * @return lista de libros encontrados en ese idioma
     * @throws IOException si ocurre un error de entrada/salida en la llamada a la API
     * @throws InterruptedException si la llamada a la API es interrumpida
     */
    public List<BookDTO> findByLanguage(String lang) throws IOException, InterruptedException {
        return apiConsumer.searchByLanguage(lang);
    }
    /**
     * Obtiene los libros más descargados, limitado a una cantidad específica.
     *
     * @param quantity cantidad máxima de libros a obtener
     * @return lista de libros más descargados
     * @throws IOException si ocurre un error de entrada/salida en la llamada a la API
     * @throws InterruptedException si la llamada a la API es interrumpida
     */
    public List<BookDTO> findTopDownloadedBooks(int quantity) throws IOException, InterruptedException {
        return apiConsumer.getTopDownloadedBooks(quantity);
    }
    /**
     * Recupera todos los libros almacenados en la base de datos local.
     *
     * @return lista de todos los libros como DTOs
     */
    @Transactional
    public List<BookDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toDTO)
                .toList();
    }
    /**
     * Guarda una lista de libros en la base de datos local si no existen.
     * Para cada libro, también verifica y crea los autores si no existen.
     *
     * @param books lista de libros a guardar
     */
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



