package cl.hugo.libreria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDto {
    private int id;
    private String title;
    private List<AuthorDto> authors;
    private List<String> languages;
    private List<String> subjects;
    private List<String> bookshelves;
    private Map<String, String> formats;

    @JsonProperty("download_count")
    @JsonAlias({"downloads","downloadCount"})
    private Integer downloadCount;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<AuthorDto> getAuthors() { return authors; }
    public void setAuthors(List<AuthorDto> authors) { this.authors = authors; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }

    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }

    public List<String> getBookshelves() { return bookshelves; }
    public void setBookshelves(List<String> bookshelves) { this.bookshelves = bookshelves; }

    public Map<String, String> getFormats() { return formats; }
    public void setFormats(Map<String, String> formats) { this.formats = formats; }

    public Integer getDownloadCount() { return downloadCount; }
    public void setDownloadCount(Integer downloadCount) { this.downloadCount = downloadCount; }

    @Override
    public String toString() {
        var author = (authors != null && !authors.isEmpty()) ? authors.get(0).getName() : "Desconocido";
        return "BookDto{id=%d, title='%s', author='%s'}".formatted(id, title, author);
    }
}