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

@Component
public class ApiConsumer {

    private static final String BASE_URL = "https://gutendex.com/books";
    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public List<BookDTO> searchByTitle(String title) throws IOException, InterruptedException {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String url = BASE_URL + "?search=" + encodedTitle;
        return fetchBooks(url);
    }

    public List<BookDTO> searchByAuthor(String author) throws IOException, InterruptedException {
        String url = BASE_URL + "?search=" + author.replace(" ", "%20");
        return fetchBooks(url);
    }

    public List<BookDTO> searchByLanguage(String lang) throws IOException, InterruptedException {
        String url = BASE_URL + "?languages=" + lang;
        return fetchBooks(url);
    }

    public List<BookDTO> getTopDownloadedBooks(int limit) throws IOException, InterruptedException {
        String url = BASE_URL + "?sort=download_count";
        List<BookDTO> topBooks = fetchBooks(url);
        return topBooks.stream().limit(limit).toList();
    }
    public List<BookDTO> searchBooksByAuthorYearRange(int startYear, int endYear) throws IOException, InterruptedException {
        String url = BASE_URL + "?author_year_start=" + startYear + "&author_year_end=" + endYear;
        return fetchBooks(url);
    }

}
