package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class BookRestClientImpl implements BookRestClient {

    private final RestClient restClient;

    public BookRestClientImpl(RestClient.Builder builder, ExternalServiceProperties properties) {
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
    }

    @Override
    public BookDto getBook(Long id) {
        try {
            BookApiResponse response = restClient.get()
                    .uri("/books/{id}", id)
                    .retrieve()
                    .body(BookApiResponse.class);

            if (response == null) {
                throw new ClientException("External service returned empty response for book id: " + id);
            }

            return mapToDto(response);
        } catch (RestClientResponseException ex) {
            throw new ClientException(
                    "External service returned " + ex.getStatusCode() + " while fetching book id: " + id,
                    ex
            );
        } catch (ResourceAccessException ex) {
            throw new ClientException("External service is unreachable while fetching book id: " + id, ex);
        } catch (RestClientException ex) {
            throw new ClientException("Could not fetch book id: " + id, ex);
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] responses = restClient.get()
                    .uri("/books")
                    .retrieve()
                    .body(BookApiResponse[].class);

            if (responses == null) {
                return List.of();
            }

            return Arrays.stream(responses)
                    .map(this::mapToDto)
                    .toList();
        } catch (RestClientResponseException ex) {
            throw new ClientException(
                    "External service returned " + ex.getStatusCode() + " while fetching all books",
                    ex
            );
        } catch (ResourceAccessException ex) {
            throw new ClientException("External service is unreachable while fetching all books", ex);
        } catch (RestClientException ex) {
            throw new ClientException("Could not fetch all books", ex);
        }
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
