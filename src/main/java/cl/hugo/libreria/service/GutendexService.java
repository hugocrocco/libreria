package cl.hugo.libreria.service;

import cl.hugo.libreria.dto.GutendexResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@Service
public class GutendexService {

    private static final String API_BASE = "https://gutendex.com/books/";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public GutendexService(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    /* ===================== Endpoints básicos ===================== */

    public GutendexResponse listar() {
        return callAndMap(API_BASE);
    }

    public GutendexResponse buscar(String q, String languages) {
        String url = API_BASE + "?search=" + enc(q);
        if (languages != null && !languages.isBlank()) url += "&languages=" + enc(languages);
        return callAndMap(url);
    }

    public GutendexResponse byIds(List<Integer> ids) {
        String joined = String.join(",", ids.stream().map(String::valueOf).toList());
        return callAndMap(API_BASE + "?ids=" + joined);
    }

    /* ===================== Transformación ===================== */

    /** Lista resumida para consola/API propia */
    public List<BookPreview> preview(String q, String languages, int limit) {
        var resp = (q == null || q.isBlank()) ? listar() : buscar(q, languages);
        return resp.getResults().stream().limit(limit).map(b -> {
            String author = (b.getAuthors() != null && !b.getAuthors().isEmpty())
                    ? b.getAuthors().get(0).getName()
                    : "Autor desconocido";
            String html = b.getFormats() != null ? b.getFormats().get("text/html") : null;
            String epub = b.getFormats() != null ? b.getFormats().get("application/epub+zip") : null;
            return new BookPreview(
                    b.getId(),
                    b.getTitle(),
                    author,
                    b.getLanguages(),
                    b.getDownloadCount(),
                    html,
                    epub
            );
        }).toList();
    }

    /** DTO liviano para preview */
    public static class BookPreview {
        private final int id;
        private final String title;
        private final String author;
        private final List<String> languages;
        private final Integer downloadCount;
        private final String html;
        private final String epub;

        public BookPreview(int id, String title, String author, List<String> languages,
                           Integer downloadCount, String html, String epub) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.languages = languages;
            this.downloadCount = downloadCount;
            this.html = html;
            this.epub = epub;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public List<String> getLanguages() { return languages; }
        public Integer getDownloadCount() { return downloadCount; }
        public String getHtml() { return html; }
        public String getEpub() { return epub; }
    }

    /* ===================== Infra HTTP ===================== */

    private GutendexResponse callAndMap(String url) {
        System.out.println("[Gutendex] GET " + url);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Literalura/1.0 (+java-httpclient)")
                    .GET()
                    .build();

            HttpResponse<String> resp = sendWithRetries(req, 3, 500, 3000);
            System.out.println("[Gutendex] status=" + resp.statusCode());

            int sc = resp.statusCode();
            if (sc < 200 || sc >= 300) {
                throw new IOException("HTTP " + sc + " body=" + safe(resp.body()));
            }
            return mapper.readValue(resp.body(), GutendexResponse.class);

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            e.printStackTrace();
            throw new RuntimeException("Error consultando/mapeando Gutendex: " + e.getMessage(), e);
        }
    }

    private HttpResponse<String> sendWithRetries(HttpRequest req, int maxAttempts, long backoffMinMs, long backoffMaxMs)
            throws IOException, InterruptedException {
        IOException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return client.send(req, HttpResponse.BodyHandlers.ofString());
            } catch (HttpTimeoutException | java.net.SocketTimeoutException e) {
                last = new IOException("timeout", e);
            } catch (IOException e) {
                last = e;
            }
            if (attempt < maxAttempts) {
                long sleep = Math.min(backoffMaxMs, backoffMinMs * attempt);
                System.out.println("[Gutendex] retry " + attempt + " in " + sleep + "ms");
                Thread.sleep(sleep);
            }
        }
        throw last != null ? last : new IOException("unknown error");
    }

    private static String enc(String s) { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
    private static String safe(String s){ return s == null ? "" : (s.length()>500 ? s.substring(0,500)+"..." : s); }
}