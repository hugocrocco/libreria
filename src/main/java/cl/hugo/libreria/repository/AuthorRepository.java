package cl.hugo.libreria.repository;

import cl.hugo.libreria.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameIgnoreCase(String name);

    // Trae autores y sus libros para evitar LazyInitializationException
    @Query("select distinct a from Author a left join fetch a.books b left join fetch b.authors")
    List<Author> findAllWithBooks();
}