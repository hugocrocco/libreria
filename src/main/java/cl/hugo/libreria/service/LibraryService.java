// src/main/java/cl/hugo/libreria/service/LibraryService.java
package cl.hugo.libreria.service;

import cl.hugo.libreria.dto.AuthorWithBooksDto;
import cl.hugo.libreria.dto.BookDto;
import cl.hugo.libreria.dto.BookSummaryDto;
import cl.hugo.libreria.model.Author;
import cl.hugo.libreria.model.Book;
import cl.hugo.libreria.repository.AuthorRepository;
import cl.hugo.libreria.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LibraryService {
    private final GutendexService gutendex;
    private final BookRepository books;
    private final AuthorRepository authors;

    public LibraryService(GutendexService gutendex, BookRepository books, AuthorRepository authors) {
        this.gutendex = gutendex;
        this.books = books;
        this.authors = authors;
    }

    @Transactional
    public Book importFirstByTitle(String title, String languages) {
        var resp = gutendex.buscar(title, (languages == null || languages.isBlank()) ? null : languages);
        if (resp.getResults() == null || resp.getResults().isEmpty()) {
            throw new NoSuchElementException("Sin resultados para: " + title);
        }
        return upsertFromDto(resp.getResults().get(0));
    }

    @Transactional
    public Book upsertFromDto(BookDto dto) {
        Book b = books.findByGutendexId(dto.getId()).orElseGet(Book::new);

        b.setGutendexId(dto.getId());
        b.setTitle(dto.getTitle());

        // Un solo idioma (regla del desafío)
        String lang = (dto.getLanguages() != null && !dto.getLanguages().isEmpty())
                ? dto.getLanguages().get(0)
                : null;
        b.setLanguage(lang);

        b.setDownloadCount(dto.getDownloadCount());

        // Normaliza autores por nombre
        var set = new LinkedHashSet<Author>();
        if (dto.getAuthors() != null) {
            for (var a : dto.getAuthors()) {
                if (a.getName() == null || a.getName().isBlank()) continue;
                var author = authors.findByNameIgnoreCase(a.getName()).orElseGet(() -> {
                    var na = new Author();
                    na.setName(a.getName());
                    na.setBirthYear(a.getBirthYear());
                    na.setDeathYear(a.getDeathYear());
                    return na;
                });
                set.add(author);
            }
        }
        b.setAuthors(set);

        return books.save(b);
    }

    // ✅ Trae autores con fetch join para evitar LazyInitializationException
    @Transactional(readOnly = true)
    public List<Book> listAll() {
        return books.findAllWithAuthors();
    }

    // ✅ Trae autores con fetch join para evitar LazyInitializationException
    @Transactional(readOnly = true)
    public List<Book> listByLanguage(String lang) {
        return books.findByLanguageWithAuthors(lang);
    }

    // ========= EXTRA: Autores con sus libros (DTOs) =========
    @Transactional(readOnly = true)
    public List<AuthorWithBooksDto> listAuthorsWithBooks() {
        var list = authors.findAllWithBooks(); // fetch join en AuthorRepository
        return list.stream()
                .map(a -> new AuthorWithBooksDto(
                        a.getName(),
                        a.getBirthYear(),
                        a.getDeathYear(),
                        a.getBooks().stream()
                                .map(b -> new BookSummaryDto(b.getId(), b.getTitle(), b.getLanguage()))
                                .sorted(Comparator.comparing(BookSummaryDto::getTitle, String.CASE_INSENSITIVE_ORDER))
                                .toList()
                ))
                .sorted(Comparator.comparing(AuthorWithBooksDto::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}