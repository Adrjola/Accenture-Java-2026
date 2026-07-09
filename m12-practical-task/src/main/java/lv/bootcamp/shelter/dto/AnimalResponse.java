package lv.bootcamp.shelter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lv.bootcamp.shelter.model.AnimalStatus;
import lv.bootcamp.shelter.model.AnimalType;

/**
 * Response body for a single animal returned by the API.
 * adoptionNote is only populated for ADMIN callers.
 */
public record AnimalResponse(
        @Schema(description = "Animal id", example = "1")
        Long id,

        @Schema(description = "Animal name", example = "Luna")
        String name,

        @Schema(description = "Animal type", example = "CAT")
        AnimalType type,

        @Schema(description = "Animal breed", example = "Bengal")
        String breed,

        @Schema(description = "Animal age in years", example = "2")
        Integer age,

        @Schema(description = "Short animal description", example = "Calm and affectionate. Loves cuddles.")
        String description,

        @Schema(description = "Current adoption status", example = "AVAILABLE")
        AnimalStatus status,

        @Schema(description = "Resolved image URL used by the UI", example = "/images/animals/Luna.jpeg")
        String imageUrl,

        @Schema(description = "Visible only for ADMIN callers when the animal is adopted", example = "adopted by user on 2026-06-01")
        String adoptionNote
) {}
