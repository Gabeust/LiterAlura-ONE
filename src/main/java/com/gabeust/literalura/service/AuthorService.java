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
/**
 * Servicio para manejar operaciones relacionadas con autores y sus libros.
 * Provee métodos para obtener autores, buscar libros por autor y gestionar persistencia.
 */
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ApiConsumer apiConsumer;
    /**
     * Constructor con inyección de dependencias.
     *
     * @param authorRepository repositorio para operaciones con autores en la base de datos
     * @param bookRepository repositorio para operaciones con libros en la base de datos
     * @param apiConsumer cliente para consumir APIs externas de búsqueda de libros
     */
    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository, ApiConsumer apiConsumer) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.apiConsumer = apiConsumer;
    }

    /**
     * Obtiene la lista de todos los autores almacenados en la base de datos.
     *
     * @return lista de DTOs de autores
     */
    public List<AuthorDTO> findAll(){
       return authorRepository.findAll().stream()
               .map(AuthorMapper::toDTO)
               .toList();
    }
    /**
     * Busca un autor en la base de datos por nombre y años de nacimiento y muerte.
     * Si no existe, lo crea y guarda.
     *
     * @param dto DTO con datos del autor
     * @return entidad Author existente o creada
     */
    public Author findOrCreate(AuthorDTO dto) {
        return authorRepository.findByNameAndBirthYearAndDeathYear(dto.name(), dto.birthYear(), dto.deathYear())
                .orElseGet(() -> authorRepository.save(AuthorMapper.toEntity(dto)));
    }

    /**
     * Busca libros por el nombre del autor usando una API externa,
     * y guarda los libros en la base de datos si no existen.
     * Además, asegura que los autores relacionados no se dupliquen en la base.
     *
     * @param authorName nombre del autor para búsqueda
     * @return lista de DTOs de libros encontrados
     * @throws InterruptedException si la operación es interrumpida
     * @throws IOException si ocurre un error de entrada/salida durante la llamada a la API
     */
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
    /**
     * Busca libros cuyos autores estuvieron vivos entre un rango de años específico,
     * usando una API externa.
     *
     * @param startYear año inicial del rango
     * @param endYear año final del rango
     * @return lista de DTOs de libros encontrados
     * @throws IOException si ocurre un error de entrada/salida durante la llamada a la API
     * @throws InterruptedException si la operación es interrumpida
     */
    public List<BookDTO> findBooksByAuthorsAliveBetween(int startYear, int endYear) throws IOException, InterruptedException {
        return apiConsumer.searchBooksByAuthorYearRange(startYear, endYear);
    }
}
