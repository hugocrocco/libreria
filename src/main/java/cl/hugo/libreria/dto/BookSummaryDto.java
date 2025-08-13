package cl.hugo.libreria.dto;

public class BookSummaryDto {
    private Long id;
    private String title;
    private String language;

    public BookSummaryDto(Long id, String title, String language) {
        this.id = id;
        this.title = title;
        this.language = language;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getLanguage() { return language; }
}