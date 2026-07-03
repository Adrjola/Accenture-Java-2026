package bootcamp.hibernate_practical.service;

import bootcamp.hibernate_practical.dto.BookResponse;
import bootcamp.hibernate_practical.dto.CreateBookRequest;
import bootcamp.hibernate_practical.dto.UpdateBookRequest;
import bootcamp.hibernate_practical.entity.Book;
import bootcamp.hibernate_practical.exception.BookNotFoundException;
import bootcamp.hibernate_practical.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse createBook(CreateBookRequest request) {
        validateCreateRequest(request);

        Book book = new Book(
                request.getTitle().trim(),
                request.getAuthor().trim(),
                request.getGenre().trim(),
                request.getPublicationYear(),
                true
        );

        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    public List<BookResponse> getAllBooks() {
        return mapToResponseList(bookRepository.findAll());
    }

    public BookResponse getBookById(Long id) {
        return mapToResponse(getBookEntityById(id));
    }

    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        validateUpdateRequest(request);

        Book book = getBookEntityById(id);
        book.setTitle(request.getTitle().trim());
        book.setAuthor(request.getAuthor().trim());
        book.setGenre(request.getGenre().trim());
        book.setPublicationYear(request.getPublicationYear());
        book.setAvailable(request.isAvailable());

        return mapToResponse(bookRepository.save(book));
    }

    public void deleteBook(Long id) {
        Book book = getBookEntityById(id);
        bookRepository.delete(book);
    }

    public List<BookResponse> findByAuthor(String author) {
        String checkedAuthor = validateText("Author", author);
        return mapToResponseList(bookRepository.findByAuthorIgnoreCase(checkedAuthor));
    }

    public List<BookResponse> findAvailableBooks() {
        return mapToResponseList(bookRepository.findByAvailableTrue());
    }

    public List<BookResponse> searchBooks(
            String title,
            String author,
            String genre,
            Integer publicationYear,
            Boolean available
    ) {
        return bookRepository.findAll().stream()
                .filter(book -> isBlank(title) || containsIgnoreCase(book.getTitle(), title))
                .filter(book -> isBlank(author) || book.getAuthor().equalsIgnoreCase(author.trim()))
                .filter(book -> isBlank(genre) || book.getGenre().equalsIgnoreCase(genre.trim()))
                .filter(book -> publicationYear == null || book.getPublicationYear() == publicationYear)
                .filter(book -> available == null || book.isAvailable() == available)
                .map(this::mapToResponse)
                .toList();
    }

    private BookResponse mapToResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPublicationYear(),
                book.isAvailable()
        );
    }

    private List<BookResponse> mapToResponseList(List<Book> books) {
        return books.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Book getBookEntityById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Book id is required");
        }

        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " was not found"));
    }

    private void validateCreateRequest(CreateBookRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book request is required");
        }

        validateText("Title", request.getTitle());
        validateText("Author", request.getAuthor());
        validateText("Genre", request.getGenre());
        validatePublicationYear(request.getPublicationYear());
    }

    private void validateUpdateRequest(UpdateBookRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Book request is required");
        }

        validateText("Title", request.getTitle());
        validateText("Author", request.getAuthor());
        validateText("Genre", request.getGenre());
        validatePublicationYear(request.getPublicationYear());
    }

    private String validateText(String fieldName, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value.trim();
    }

    private void validatePublicationYear(int publicationYear) {
        if (publicationYear <= 0) {
            throw new IllegalArgumentException("Publication year must be greater than 0");
        }
    }

    private boolean containsIgnoreCase(String text, String searchValue) {
        return text.toLowerCase().contains(searchValue.trim().toLowerCase());
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
