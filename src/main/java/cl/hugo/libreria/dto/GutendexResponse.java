package cl.hugo.libreria.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexResponse {
    private int count;
    private String next;
    private String previous;
    private List<BookDto> results;

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public String getNext() { return next; }
    public void setNext(String next) { this.next = next; }

    public String getPrevious() { return previous; }
    public void setPrevious(String previous) { this.previous = previous; }

    public List<BookDto> getResults() { return results; }
    public void setResults(List<BookDto> results) { this.results = results; }

    @Override
    public String toString() {
        return "GutendexResponse{count=%d}".formatted(count);
    }
}