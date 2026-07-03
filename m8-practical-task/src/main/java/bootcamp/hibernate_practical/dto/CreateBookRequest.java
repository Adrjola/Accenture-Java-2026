package bootcamp.hibernate_practical.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookRequest {
    private String title;
    private String author;
    private String genre;
    private int publicationYear;
}
