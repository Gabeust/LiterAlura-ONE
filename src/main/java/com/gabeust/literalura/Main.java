package com.gabeust.literalura;

import com.gabeust.literalura.service.AuthorService;
import com.gabeust.literalura.service.BookPrinterService;
import com.gabeust.literalura.service.BookService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
/**
 * Componente principal que ejecuta el menú interactivo para la aplicación LiterAlura.
 * Permite al usuario buscar y listar libros y autores mediante la consola.
 */
@Component
public class Main {

    private final BookService bookService;
    private final AuthorService authorService;
    private final BookPrinterService printerService;

    public Main(BookService bookService, AuthorService authorService, BookPrinterService printerService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.printerService = printerService;
    }
    /**
     * Método que se ejecuta automáticamente después de la creación del bean Spring.
     * Muestra un menú en consola con diferentes opciones para interactuar con la aplicación.
     *
     * @throws Exception en caso de error general durante la ejecución del menú.
     */
    @PostConstruct
    public void runMenu() throws Exception {
        Scanner scanner = new Scanner(System.in);
        int option = 0;

        do {
            System.out.println("\n" + "═".repeat(60));
            System.out.println("📚 Bienvenido/a a LiterAlura 📚");
            System.out.println("═".repeat(60));
            System.out.println("Seleccione una opción del menú:\n");

            System.out.println("""
                    1️⃣  Buscar libro por título
                    2️⃣  Buscar libros por autor
                    3️⃣  Listar autores registrados
                    4️⃣  Listar libros registrados
                    5️⃣  Libros con autores vivos en un rango de años
                    6️⃣  Listar libros por idioma
                    7️⃣  Top 10 libros más descargados
                    8️⃣  Salir
                    """);

            System.out.print("👉 Ingrese una opción: ");
            String input = scanner.nextLine();
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Opción no válida. Por favor, ingrese un número del 1 al 8.");
                continue;
            }

            System.out.println("\n" + "-".repeat(60));

            switch (option) {
                case 1 -> {
                    System.out.print("🔍 Ingrese el título del libro: ");
                    String title = scanner.nextLine();
                    var bookOpt = bookService.findByTitle(title);
                    if (bookOpt.isPresent()) {
                        printerService.printBooks(List.of(bookOpt.get()));
                    } else {
                        System.out.println("📕 No se encontró ningún libro con ese título.");
                    }
                }
                case 2 -> {
                    System.out.print("✍️ Ingrese el nombre del autor: ");
                    String author = scanner.nextLine();

                    var booksByAuthor = authorService.findByAuthor(author);
                    if (booksByAuthor.isEmpty()) {
                        System.out.println("📕 No se encontraron libros para ese autor.");
                    } else {
                        bookService.saveAllIfNotExists(booksByAuthor);
                        printerService.printBooks(booksByAuthor);
                    }
                }
                case 3 -> {
                    System.out.println("👥 Sus autores registrados:");
                    var authors = authorService.findAll();
                    printerService.printAuthors(authors);
                }
                case 4 -> {
                    System.out.println("📚 Libros registrados:");
                    var books = bookService.findAll();
                    printerService.printBooks(books);
                }
                case 5 -> {
                    System.out.print("📅 Ingrese el año de inicio: ");
                    int startYear = Integer.parseInt(scanner.nextLine());
                    System.out.print("📅 Ingrese el año de fin: ");
                    int endYear = Integer.parseInt(scanner.nextLine());

                    var books = authorService.findBooksByAuthorsAliveBetween(startYear, endYear);
                    if (books.isEmpty()) {
                        System.out.println("📕 No se encontraron libros con autores vivos en ese rango.");
                    } else {
                        System.out.println("📚 Resultados encontrados:\n");
                        books.forEach(book -> {
                            printerService.printBooks(List.of(book));
                            System.out.println("✍️ Autor(es):");
                            book.authors().forEach(printerService::printAuthor);
                            System.out.println("-".repeat(50));
                        });
                    }
                }
                case 6 -> {
                    System.out.println("🌐 Seleccione el idioma:");
                    System.out.println("""
                            1️⃣  Inglés (en)
                            2️⃣  Español (es)
                            3️⃣  Francés (fr)
                            4️⃣  Alemán (de)
                            5️⃣  Latín (la)
                            """);
                    System.out.print("👉 Ingrese una opción: ");
                    int languageOption = scanner.nextInt();
                    scanner.nextLine(); // limpia buffer

                    String selectedLanguage;
                    switch (languageOption) {
                        case 1 -> selectedLanguage = "en";
                        case 2 -> selectedLanguage = "es";
                        case 3 -> selectedLanguage = "fr";
                        case 4 -> selectedLanguage = "de";
                        case 5 -> selectedLanguage = "la";
                        default -> {
                            System.out.println("⚠️ Opción no válida. Se usará 'en' por defecto.");
                            selectedLanguage = "en";
                        }
                    }

                    var booksByLanguage = bookService.findByLanguage(selectedLanguage);
                    if (booksByLanguage.isEmpty()) {
                        System.out.println("📕 No se encontraron libros en el idioma seleccionado.");
                    } else {
                        printerService.printBooks(booksByLanguage);
                    }
                }
                case 7 -> {
                    System.out.println("📈 Top 10 libros más descargados:");
                    var topBooks = bookService.findTopDownloadedBooks(10);
                    printerService.printBooks(topBooks);
                }
                case 8 -> {
                    System.out.println("\n👋 Gracias por usar LiterAlura. ¡Hasta luego!");
                }
                default -> System.out.println("❌ Opción no válida.");
            }

            System.out.println("\n" + "=".repeat(60));
            Thread.sleep(2500); // Espera breve para mejorar UX

        } while (option != 8);
    }

}
