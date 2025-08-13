package cl.hugo.libreria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorDto {
    private String name;

    @JsonProperty("birth_year")
    @JsonAlias({"birthYear"})
    private Integer birthYear;

    @JsonProperty("death_year")
    @JsonAlias({"deathYear"})
    private Integer deathYear;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }

    public Integer getDeathYear() { return deathYear; }
    public void setDeathYear(Integer deathYear) { this.deathYear = deathYear; }

    @Override
    public String toString() {
        return "Author{name='%s', %s-%s}".formatted(
                name,
                birthYear == null ? "?" : birthYear,
                deathYear == null ? "?" : deathYear
        );
    }
}