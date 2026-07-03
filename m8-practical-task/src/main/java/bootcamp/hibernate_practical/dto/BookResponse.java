package bootcamp.hibernate_practical.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private String genre;
    private int publicationYear;
    private boolean available;
}
