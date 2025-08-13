package cl.hugo.libreria.repository;

import cl.hugo.libreria.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByGutendexId(Integer gutendexId);

    // Para búsquedas locales
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Traer autores en la misma consulta
    @Query("select distinct b from Book b left join fetch b.authors")
    List<Book> findAllWithAuthors();

    @Query("select distinct b from Book b left join fetch b.authors " +
            "where upper(b.language) = upper(?1)")
    List<Book> findByLanguageWithAuthors(String language);
}