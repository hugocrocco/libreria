package cl.hugo.libreria;

import cl.hugo.libreria.dto.AuthorWithBooksDto;
import cl.hugo.libreria.dto.BookSummaryDto;
import cl.hugo.libreria.service.GutendexService;
import cl.hugo.libreria.service.GutendexService.BookPreview;
import cl.hugo.libreria.service.LibraryService;
import cl.hugo.libreria.model.Book;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
public class LibreriaApplication implements CommandLineRunner {

	private final GutendexService gutendexService;
	private final LibraryService libraryService;

	public LibreriaApplication(GutendexService gutendexService, LibraryService libraryService) {
		this.gutendexService = gutendexService;
		this.libraryService = libraryService;
	}

	public static void main(String[] args) {
		SpringApplication.run(LibreriaApplication.class, args);
	}

	@Override
	public void run(String... args) {
		mostrarMenu();
	}

	private void mostrarMenu() {
		try (Scanner sc = new Scanner(System.in)) {
			while (true) {
				System.out.println("\n===== Libreria de Hugo Crocco - Menú =====");
				System.out.println("1) Listar libros (primeros 10) desde la API");
				System.out.println("2) Buscar por texto (q) y opcional idioma (languages) en la API");
				System.out.println("3) Solo títulos (autocompletar) desde la API");
				System.out.println("4) Buscar y mostrar detalles (id, título, autor, html/epub) desde la API");
				System.out.println("--- Desde la Base de Datos  (PostgreSQL) ---");
				System.out.println("10) Importar PRIMER resultado por título desde la API y GUARDAR en BD");
				System.out.println("11) Listar TODOS los libros guardados en BD");
				System.out.println("12) Listar libros por IDIOMA desde BD (ej: es, en, fr)");
				System.out.println("13) Listar AUTORES con sus LIBROS (BD)");
				System.out.println("0) Salir");
				System.out.print("Elige una opción: ");

				String opcion = sc.nextLine().trim();
				try {
					switch (opcion) {
						case "1" -> opcionListarApi();
						case "2" -> opcionBuscarApi(sc);
						case "3" -> opcionTitulosApi(sc);
						case "4" -> opcionPreviewApi(sc);
						case "10" -> opcionImportarPorTituloBD(sc);
						case "11" -> opcionListarTodosBD();
						case "12" -> opcionListarPorIdiomaBD(sc);
						case "13" -> opcionListarAutoresConLibros();
						case "0" -> {
							System.out.println("¡Hasta luego!");
							return;
						}
						default -> System.out.println("Opción inválida. Intenta de nuevo.");
					}
				} catch (Exception e) {
					System.out.println("Ocurrió un error: " + e.getMessage());
				}
			}
		}
	}

	/* ===================== API (Gutendex) ===================== */

	private void opcionListarApi() {
		var resp = gutendexService.listar();
		var results = resp.getResults();
		System.out.printf("Total en API: %d | Mostrando: %d%n",
				resp.getCount(), Math.min(10, results.size()));
		results.stream().limit(10).forEach(b ->
				System.out.printf("• [%d] %s — %s%n",
						b.getId(),
						b.getTitle(),
						(b.getAuthors() != null && !b.getAuthors().isEmpty())
								? b.getAuthors().get(0).getName()
								: "Autor desconocido")
		);
		;
	}

	private void opcionBuscarApi(Scanner sc) {
		System.out.print("Texto a buscar (q): ");
		String q = sc.nextLine().trim();
		System.out.print("Idiomas (ej: es o en,es) [Enter para omitir]: ");
		String languages = sc.nextLine().trim();

		var resp = gutendexService.buscar(q, languages.isBlank() ? null : languages);
		System.out.printf("Coincidencias: %d%n", resp.getResults().size());
		resp.getResults().stream().limit(10).forEach(b ->
				System.out.printf("• [%d] %s — %s%n",
						b.getId(),
						b.getTitle(),
						(b.getAuthors() != null && !b.getAuthors().isEmpty())
								? b.getAuthors().get(0).getName()
								: "Autor desconocido")
		);
	}

	private void opcionTitulosApi(Scanner sc) {
		System.out.print("Texto a buscar (q): ");
		String q = sc.nextLine().trim();
		System.out.print("Idiomas (opcional): ");
		String languages = sc.nextLine().trim();

		var titulos = gutendexService.buscar(q, languages.isBlank() ? null : languages)
				.getResults()
				.stream()
				.limit(10)
				.map(b -> b.getTitle())
				.collect(Collectors.toList());

		if (titulos.isEmpty()) {
			System.out.println("No se encontraron títulos.");
		} else {
			titulos.forEach(t -> System.out.println("• " + t));
		}
	}

	private void opcionPreviewApi(Scanner sc) {
		System.out.print("Texto a buscar (q) [Enter para listar]: ");
		String q = sc.nextLine().trim();
		System.out.print("Idiomas (opcional): ");
		String languages = sc.nextLine().trim();

		List<BookPreview> lista = gutendexService.preview(
				q.isBlank() ? null : q,
				languages.isBlank() ? null : languages,
				10
		);
		if (lista.isEmpty()) {
			System.out.println("Sin resultados.");
			return;
		}
		lista.forEach(p -> System.out.printf(
				"[%d] %s — %s | langs=%s | descargas=%s%n   html: %s%n   epub: %s%n",
				p.getId(), p.getTitle(), p.getAuthor(), p.getLanguages(),
				p.getDownloadCount(), p.getHtml(), p.getEpub()
		));
	}

	/* ===================== Base de Datos (PostgreSQL) ===================== */

	private void opcionImportarPorTituloBD(Scanner sc) {
		System.out.print("Título a buscar en la API: ");
		String title = sc.nextLine().trim();
		System.out.print("Idiomas (opcional, ej: es o en,es): ");
		String languages = sc.nextLine().trim();

		Book b = libraryService.importFirstByTitle(title, languages.isBlank() ? null : languages);
		var author = b.getAuthors().stream().findFirst().map(a -> a.getName()).orElse("Autor desconocido");
		System.out.printf("Guardado en BD: [%d] %s — %s | lang=%s | descargas=%s%n",
				b.getId(), b.getTitle(), author, b.getLanguage(), b.getDownloadCount());
	}

	private void opcionListarTodosBD() {
		var list = libraryService.listAll();
		if (list.isEmpty()) {
			System.out.println("No hay libros guardados en la BD.");
			return;
		}
		list.forEach(b -> {
			var author = b.getAuthors().stream().findFirst().map(a -> a.getName()).orElse("Autor desconocido");
			System.out.printf("[%d] %s — %s | lang=%s | descargas=%s%n",
					b.getId(), b.getTitle(), author, b.getLanguage(), b.getDownloadCount());
		});
	}

	private void opcionListarPorIdiomaBD(Scanner sc) {
		System.out.print("Idioma (ej: es, en, fr): ");
		String lang = sc.nextLine().trim();
		var list = libraryService.listByLanguage(lang);
		if (list.isEmpty()) {
			System.out.println("Sin resultados para idioma: " + lang);
			return;
		}
		list.forEach(b -> {
			var author = b.getAuthors().stream().findFirst().map(a -> a.getName()).orElse("Autor desconocido");
			System.out.printf("[%d] %s — %s | lang=%s%n", b.getId(), b.getTitle(), author, b.getLanguage());
		});
	}

	/* ===== EXTRA: Autores con sus libros ===== */
	private void opcionListarAutoresConLibros() {
		var lista = libraryService.listAuthorsWithBooks();
		if (lista.isEmpty()) {
			System.out.println("No hay autores en la BD.");
			return;
		}
		lista.forEach(a -> {
			int count = a.getBooks() != null ? a.getBooks().size() : 0;
			System.out.printf("- %s (%s–%s) | %d libro(s)%n",
					a.getName(),
					a.getBirthYear() != null ? a.getBirthYear() : "?",
					a.getDeathYear() != null ? a.getDeathYear() : "?",
					count);
			if (count > 0) {
				a.getBooks().forEach(b ->
						System.out.printf("    · [%d] %s (lang=%s)%n",
								b.getId(), b.getTitle(), b.getLanguage())
				);
			}
		});
	}
}