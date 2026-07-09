package lv.bootcamp.shelter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lv.bootcamp.shelter.model.AnimalType;

/**
 * JSON request body for creating a new animal via the REST API.
 * Status is not included; all new animals start as AVAILABLE.
 */
public record AnimalCreateRequest(

        @Schema(description = "Animal name", example = "Milo")
        @NotBlank(message = "Name is required")
        String name,

        @Schema(description = "Animal type", example = "CAT")
        @NotNull(message = "Type is required")
        AnimalType type,

        @Schema(description = "Animal breed", example = "Siamese")
        String breed,

        @Schema(description = "Animal age in years", example = "3")
        @Min(value = 0, message = "Age cannot be negative")
        Integer age,

        @Schema(description = "Short animal description", example = "Friendly and calm")
        String description,

        @Schema(description = "Image filename from static resources or full image URL", example = "Luna.jpeg")
        String imageUrl
) {}
