package cl.hugo.libreria.controller;

import cl.hugo.libreria.dto.BookDto;
import cl.hugo.libreria.dto.GutendexResponse;
import cl.hugo.libreria.service.GutendexService;
import cl.hugo.libreria.service.GutendexService.BookPreview;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/libros")
public class GutendexController {

    private final GutendexService service;

    public GutendexController(GutendexService service) {
        this.service = service;
    }

    //  Listado base (DTO completo mapeado)
    @GetMapping
    public GutendexResponse listar() {
        return service.listar();
    }

    //  Búsqueda (DTO completo mapeado)
    @GetMapping("/buscar")
    public GutendexResponse buscar(@RequestParam String q,
                                   @RequestParam(required = false) String languages) {
        return service.buscar(q, languages);
    }


    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }


    @GetMapping("/preview")
    public List<BookPreview> preview(@RequestParam(required = false) String q,
                                     @RequestParam(required = false) String languages,
                                     @RequestParam(defaultValue = "10") int limit) {
        return service.preview(q, languages, limit);
    }


    @GetMapping("/titulos")
    public List<String> titulos(@RequestParam String q,
                                @RequestParam(required = false) String languages,
                                @RequestParam(defaultValue = "10") int limit) {
        return service.buscar(q, languages)
                .getResults()
                .stream()
                .limit(limit)
                .map(BookDto::getTitle)
                .collect(Collectors.toList());
    }
}