package com.gabeust.literalura.service;

import com.gabeust.literalura.dto.AuthorDTO;
import com.gabeust.literalura.dto.BookDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
/**
 * Servicio responsable de imprimir en consola información formateada
 * sobre libros y autores.
 */
@Service
public class BookPrinterService {
    /**
     * Imprime una lista de libros con detalles como título, autores,
     * resúmenes, temas, idiomas y cantidad de descargas.
     *
     * @param books lista de DTOs de libros a imprimir
     */
    public void printBooks(List<BookDTO> books) {
        if (books == null || books.isEmpty()) {
            System.out.println("📚 No se encontraron libros.");
            return;
        }

        for (BookDTO book : books) {
            System.out.println("=".repeat(60));
            System.out.println("📖 Título: " + book.title());

            String authors = book.authors().stream()
                    .map(AuthorDTO::name)
                    .collect(Collectors.joining(", "));
            System.out.println("✍️ Autor(es): " + authors);

            System.out.println("📝 Resumen(es):");
            book.summaries().forEach(s -> System.out.println("   " + s));

            System.out.println("📚 Temas: " + String.join(", ", book.subjects()));
            System.out.println("🗣 Idiomas: " + String.join(", ", book.languages()));
            System.out.println("⬇️ Descargas: " + book.download_count());
        }
    }
    /**
     * Imprime una lista de autores con sus detalles básicos.
     *
     * @param authors lista de DTOs de autores a imprimir
     */
    public void printAuthors(List<AuthorDTO> authors) {
        if (authors == null || authors.isEmpty()) {
            System.out.println("✍️ No se encontraron autores.");
            return;
        }

        for (AuthorDTO author : authors) {
            System.out.println("=".repeat(60));
            printAuthor(author); // reutiliza el método individual
        }
    }
    /**
     * Imprime la información detallada de un autor.
     *
     * @param author DTO del autor a imprimir
     */
    public void printAuthor(AuthorDTO author) {
        System.out.println("👤 Nombre: " + author.name());
        System.out.println("📅 Año de nacimiento: " + (author.birthYear() != null ? author.birthYear() : "Desconocido"));
        System.out.println("🪦 Año de fallecimiento: " + (author.deathYear() != null ? author.deathYear() : "Desconocido"));
        System.out.println("-".repeat(50));
    }
}