package cl.hugo.libreria.model;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "authors", indexes = @Index(name="idx_author_name", columnList="name"))
public class Author {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer birthYear;
    private Integer deathYear;

    // Relación inversa para poder fetch-join desde Author
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new LinkedHashSet<>();

    // Getters & Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
    public Integer getDeathYear() { return deathYear; }
    public void setDeathYear(Integer deathYear) { this.deathYear = deathYear; }
    public Set<Book> getBooks() { return books; }
    public void setBooks(Set<Book> books) { this.books = books; }

    @Override public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Author a)) return false;
        return Objects.equals(name, a.name);
    }
    @Override public int hashCode(){ return Objects.hash(name); }
}