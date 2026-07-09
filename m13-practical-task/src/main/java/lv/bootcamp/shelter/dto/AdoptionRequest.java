package lv.bootcamp.shelter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for adopting an animal from the shelter.
 */
public record AdoptionRequest(
        @NotNull Long animalId,
        @NotBlank String adopterName,
        @NotBlank @Email String adopterEmail
) {
}
