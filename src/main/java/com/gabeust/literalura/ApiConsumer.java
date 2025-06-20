package com.gabeust.literalura;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabeust.literalura.dto.BookDTO;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
 * Componente responsable de consumir la API pública de Gutendex para obtener
 * información sobre libros mediante llamadas HTTP.
 */
@Component
public class ApiConsumer {

    private static final String BASE_URL = "https://gutendex.com/books";
    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Realiza la llamada HTTP a la URL especificada y parsea la respuesta JSON
     * para devolver una lista de BookDTO.
     *
     * @param url URL completa para hacer la solicitud GET
     * @return lista de libros obtenidos desde la API
     * @throws IOException          si ocurre un error al leer la respuesta
     * @throws InterruptedException si la llamada es interrumpida
     */
    private List<BookDTO> fetchBooks(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error en la llamada API: " + response.statusCode());
        }

        // parsear JSON después de validar que el status es 200
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode results = root.get("results");

        List<BookDTO> books = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode bookNode : results) {
                BookDTO book = objectMapper.treeToValue(bookNode, BookDTO.class);
                books.add(book);
            }
        }
        return books;
    }
    /**
     * Busca libros cuyo título coincida con el parámetro proporcionado.
     *
     * @param title título o parte del título a buscar
     * @return lista de libros que coinciden con el título
     * @throws IOException          si hay error de entrada/salida
     * @throws InterruptedException si la llamada HTTP es interrumpida
     */
    public List<BookDTO> searchByTitle(String title) throws IOException, InterruptedException {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = BASE_URL + "?search=" + encodedTitle;
        return fetchBooks(url);
    }
    /**
     * Busca libros por nombre del autor.
     *
     * @param author nombre o parte del nombre del autor
     * @return lista de libros escritos por el autor
     * @throws IOException          si hay error de entrada/salida
     * @throws InterruptedException si la llamada HTTP es interrumpida
     */
    public List<BookDTO> searchByAuthor(String author) throws IOException, InterruptedException {
        String url = BASE_URL + "?search=" + author.replace(" ", "%20");
        return fetchBooks(url);
    }
    /**
     * Busca libros por idioma.
     *
     * @param lang código de idioma (por ejemplo, "en" para inglés)
     * @return lista de libros en el idioma especificado
     * @throws IOException          si hay error de entrada/salida
     * @throws InterruptedException si la llamada HTTP es interrumpida
     */
    public List<BookDTO> searchByLanguage(String lang) throws IOException, InterruptedException {
        String url = BASE_URL + "?languages=" + lang;
        return fetchBooks(url);
    }
    /**
     * Obtiene los libros más descargados, limitado a la cantidad solicitada.
     *
     * @param limit número máximo de libros a devolver
     * @return lista de libros ordenados por cantidad de descargas
     * @throws IOException          si hay error de entrada/salida
     * @throws InterruptedException si la llamada HTTP es interrumpida
     */
    public List<BookDTO> getTopDownloadedBooks(int limit) throws IOException, InterruptedException {
        String url = BASE_URL + "?sort=download_count";
        List<BookDTO> topBooks = fetchBooks(url);
        return topBooks.stream().limit(limit).toList();
    }
    /**
     * Busca libros cuyos autores hayan nacido en un rango de años especificado.
     *
     * @param startYear año de inicio del rango
     * @param endYear   año final del rango
     * @return lista de libros escritos por autores vivos entre esos años
     * @throws IOException          si hay error de entrada/salida
     * @throws InterruptedException si la llamada HTTP es interrumpida
     */
    public List<BookDTO> searchBooksByAuthorYearRange(int startYear, int endYear) throws IOException, InterruptedException {
        String url = BASE_URL + "?author_year_start=" + startYear + "&author_year_end=" + endYear;
        return fetchBooks(url);
    }

}
