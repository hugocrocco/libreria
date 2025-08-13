package cl.hugo.libreria.controller;
import cl.hugo.libreria.dto.AuthorWithBooksDto;
import cl.hugo.libreria.model.Book;
import cl.hugo.libreria.service.LibraryService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/catalogo")
public class LibraryController {

    private final LibraryService service;
    public LibraryController(LibraryService service){ this.service = service; }

    // Importa PRIMER resultado por título y lo guarda
    @PostMapping("/import-by-title")
    public Book importByTitle(@RequestParam String title,
                              @RequestParam(required=false) String languages){
        return service.importFirstByTitle(title, languages);
    }

    // Lista todos
    @GetMapping("/books")
    public List<Book> all(){ return service.listAll(); }

    // Lista por idioma ( un solo idioma por libro)
    @GetMapping("/books/by-language")
    public List<Book> byLang(@RequestParam String language){
        return service.listByLanguage(language);
    }
    @GetMapping("/authors")
    public List<AuthorWithBooksDto> authorsWithBooks() {
        return service.listAuthorsWithBooks();
    }
}