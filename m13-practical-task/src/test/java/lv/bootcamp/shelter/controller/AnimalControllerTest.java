package lv.bootcamp.shelter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.model.AnimalStatus;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalNotFoundException;
import lv.bootcamp.shelter.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Task: REST controller tests with MockMvc and @WebMvcTest.
 *
 * Stub the service with @MockitoBean. Use mockMvc.perform() to make requests
 * and chain .andExpect() calls to verify status, JSON content, and error responses.
 */
@WebMvcTest(AnimalController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnimalService animalService;

    @Test
    void findAll_shouldReturnListOfAnimals() throws Exception {
        when(animalService.findAll()).thenReturn(List.of(
                animalResponse(1L, "Rex", AnimalType.DOG, AnimalStatus.AVAILABLE),
                animalResponse(2L, "Mia", AnimalType.CAT, AnimalStatus.ADOPTED)
        ));

        mockMvc.perform(get("/api/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Rex"))
                .andExpect(jsonPath("$[1].name").value("Mia"));
    }

    @Test
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(animalService.findById(99L)).thenThrow(new AnimalNotFoundException(99L));

        mockMvc.perform(get("/api/animals/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201WithCreatedAnimal() throws Exception {
        AnimalCreateRequest request = new AnimalCreateRequest(
                "Rex", AnimalType.DOG, "Labrador", 4, "Friendly");
        when(animalService.create(any())).thenReturn(
                animalResponse(1L, "Rex", AnimalType.DOG, AnimalStatus.AVAILABLE));

        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rex"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void create_shouldReturn400WhenNameIsBlank() throws Exception {
        AnimalCreateRequest request = new AnimalCreateRequest(
                "", AnimalType.DOG, "Labrador", 4, "Friendly");

        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(animalService);
        // (no stub needed — validation rejects the request before the service is called)
    }

    @Test
    void create_shouldReturn400WhenTypeIsNull() throws Exception {
        AnimalCreateRequest request = new AnimalCreateRequest(
                "Rex", null, "Labrador", 4, "Friendly");

        mockMvc.perform(post("/api/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(animalService);
    }

    private AnimalResponse animalResponse(Long id, String name, AnimalType type, AnimalStatus status) {
        return new AnimalResponse(id, name, type, "Mixed", 3, "Friendly", status);
    }
}
