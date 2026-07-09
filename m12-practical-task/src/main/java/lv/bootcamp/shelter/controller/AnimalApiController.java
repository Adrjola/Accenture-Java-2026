package lv.bootcamp.shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "Animals", description = "REST endpoints for shelter animal management")
public class AnimalApiController {

    private final AnimalService animalService;

    @Operation(
            summary = "List all animals",
            description = "Returns all shelter animals with their current adoption status."
    )
    @ApiResponse(responseCode = "200", description = "Animals returned")
    @GetMapping("/animals")
    public List<AnimalResponse> findAll() {
        return animalService.findAll();
    }

    @Operation(
            summary = "Find animal by id",
            description = "Returns one animal when the id exists."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal found"),
            @ApiResponse(responseCode = "404", description = "Animal not found", content = @Content)
    })
    @GetMapping("/animals/{id}")
    public ResponseEntity<AnimalResponse> findById(
            @Parameter(description = "Animal id", example = "1")
            @PathVariable Long id) {
        return animalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "List animal types",
            description = "Returns the supported values for animal type dropdowns."
    )
    @ApiResponse(responseCode = "200", description = "Animal types returned")
    @GetMapping("/animal-types")
    public List<AnimalType> findAnimalTypes() {
        return List.of(AnimalType.values());
    }

    @Operation(
            summary = "List adopted animals",
            description = "Returns only adopted animals. Requires ADMIN."
    )
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Adopted animals returned"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Admin role required", content = @Content)
    })
    @GetMapping("/animals/adopted")
    public List<AnimalResponse> findAdopted() {
        return animalService.findAdopted();
    }

    @Operation(
            summary = "Create animal",
            description = "Creates a new available animal. Requires ADMIN and can be tested from Swagger UI with Basic Auth."
    )
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Animal created"),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Admin role required", content = @Content)
    })
    @PostMapping("/animals")
    @ResponseStatus(HttpStatus.CREATED)
    public AnimalResponse create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Animal data to create",
                    required = true
            )
            @RequestBody @Valid AnimalCreateRequest request) {
        return animalService.create(request);
    }

    @Operation(
            summary = "Adopt animal",
            description = "Marks an available animal as adopted by the authenticated USER account."
    )
    @SecurityRequirement(name = "basicAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Animal adopted"),
            @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content),
            @ApiResponse(responseCode = "403", description = "User role required", content = @Content),
            @ApiResponse(responseCode = "404", description = "Animal not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Animal already adopted", content = @Content)
    })
    @PostMapping("/animals/{id}/adopt")
    public ResponseEntity<AnimalResponse> adopt(
            @Parameter(description = "Animal id", example = "1")
            @PathVariable Long id,
            Authentication authentication) {
        return animalService.adopt(id, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleAlreadyAdopted(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
