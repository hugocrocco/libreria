package cl.hugo.libreria.dto;

import java.util.List;

public class AuthorWithBooksDto {
    private String name;
    private Integer birthYear;
    private Integer deathYear;
    private List<BookSummaryDto> books;

    public AuthorWithBooksDto(String name, Integer birthYear, Integer deathYear, List<BookSummaryDto> books) {
        this.name = name;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
        this.books = books;
    }

    public String getName() { return name; }
    public Integer getBirthYear() { return birthYear; }
    public Integer getDeathYear() { return deathYear; }
    public List<BookSummaryDto> getBooks() { return books; }
}