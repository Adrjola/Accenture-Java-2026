package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import java.util.List;
import org.springframework.core.codec.DecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BookWebClientImpl implements BookWebClient {

    private final WebClient webClient;

    public BookWebClientImpl(WebClient.Builder builder, ExternalServiceProperties properties) {
        this.webClient = builder.baseUrl(properties.baseUrl()).build();
    }

    @Override
    public Mono<BookDto> getBookAsync(Long id) {
        return webClient.get()
                .uri("/books/{id}", id)
                .retrieve()
                .bodyToMono(BookApiResponse.class)
                .switchIfEmpty(Mono.error(new ClientException("External service returned empty response for book id: " + id)))
                .map(this::mapToDto)
                .onErrorMap(WebClientResponseException.class, ex -> new ClientException(
                        "External service returned " + ex.getStatusCode() + " while fetching book id: " + id,
                        ex
                ))
                .onErrorMap(WebClientRequestException.class, ex -> new ClientException(
                        "External service is unreachable while fetching book id: " + id,
                        ex
                ))
                .onErrorMap(DecodingException.class, ex -> new ClientException(
                        "Could not read external service response for book id: " + id,
                        ex
                ));
    }

    @Override
    public Flux<BookDto> getAllBooksAsync() {
        return webClient.get()
                .uri("/books")
                .retrieve()
                .bodyToFlux(BookApiResponse.class)
                .map(this::mapToDto)
                .onErrorMap(WebClientResponseException.class, ex -> new ClientException(
                        "External service returned " + ex.getStatusCode() + " while fetching all books",
                        ex
                ))
                .onErrorMap(WebClientRequestException.class, ex -> new ClientException(
                        "External service is unreachable while fetching all books",
                        ex
                ))
                .onErrorMap(DecodingException.class, ex -> new ClientException(
                        "Could not read external service response while fetching all books",
                        ex
                ));
    }

    @Override
    public Mono<List<BookDto>> getBooksInParallel(Long id1, Long id2) {
        return Mono.zip(getBookAsync(id1), getBookAsync(id2))
                .map(result -> List.of(result.getT1(), result.getT2()));
    }

    private BookDto mapToDto(BookApiResponse response) {
        return new BookDto(
                response.title(),
                response.author(),
                response.genre(),
                response.price()
        );
    }
}
