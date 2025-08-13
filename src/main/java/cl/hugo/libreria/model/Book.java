package cl.hugo.libreria.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name="books", indexes={
        @Index(name="idx_book_gutendex", columnList="gutendex_id", unique=true),
        @Index(name="idx_book_title", columnList="title"),
        @Index(name="idx_book_language", columnList="language")
})
public class Book {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="gutendex_id", nullable=false, unique=true) private Integer gutendexId;
    @Column(nullable=false) private String title;
    @Column(length=10) private String language;                 // un solo idioma
    @Column(name="download_count") private Integer downloadCount;

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="book_author",
            joinColumns=@JoinColumn(name="book_id"),
            inverseJoinColumns=@JoinColumn(name="author_id"))
    private Set<Author> authors = new LinkedHashSet<>();

    public Long getId(){return id;} public Integer getGutendexId(){return gutendexId;}
    public void setGutendexId(Integer x){this.gutendexId=x;}
    public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
    public String getLanguage(){return language;} public void setLanguage(String l){this.language=l;}
    public Integer getDownloadCount(){return downloadCount;} public void setDownloadCount(Integer d){this.downloadCount=d;}
    public Set<Author> getAuthors(){return authors;} public void setAuthors(Set<Author> a){this.authors=a;}
}