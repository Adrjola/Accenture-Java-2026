package bootcamp.hibernate_practical.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequest {
    private String title;
    private String author;
    private String genre;
    private int publicationYear;
    private boolean available;
}
