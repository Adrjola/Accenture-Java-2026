package lv.bootcamp.shelter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnimalPageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeAndAnimalListArePublic() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Welcome to the Shelter")));

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Shelter Animals")))
                .andExpect(content().string(containsString("Luna")))
                .andExpect(content().string(containsString("href=\"/animals/1\"")))
                .andExpect(content().string(containsString("badge-available")));
    }

    @Test
    void animalListCanBeFilteredByType() throws Exception {
        mockMvc.perform(get("/animals").param("type", "CAT"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Luna")))
                .andExpect(content().string(not(containsString("Diora"))));
    }

    @Test
    void animalDetailPageShowsAnimalAndMissingAnimalReturnsNotFound() throws Exception {
        mockMvc.perform(get("/animals/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Luna")))
                .andExpect(content().string(containsString("Bengal")))
                .andExpect(content().string(containsString("Back to list")));

        mockMvc.perform(get("/animals/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addAnimalFormRequiresLogin() throws Exception {
        mockMvc.perform(get("/animals/new"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void animalTypesApiReturnsAvailableTypes() throws Exception {
        mockMvc.perform(get("/api/animal-types"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CAT")))
                .andExpect(content().string(containsString("DOG")))
                .andExpect(content().string(containsString("OTHER")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanOpenAndSubmitAddAnimalForm() throws Exception {
        mockMvc.perform(get("/animals/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add a new animal")))
                .andExpect(content().string(containsString("fetch('/api/animal-types')")));

        mockMvc.perform(post("/animals")
                        .with(csrf())
                        .param("name", "Milo")
                        .param("type", "CAT")
                        .param("breed", "Siamese")
                        .param("age", "3")
                        .param("description", "Friendly and calm")
                        .param("imageUrl", "Luna.jpeg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animals"))
                .andExpect(flash().attribute("message", "Animal added!"));

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Milo")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidAddAnimalFormShowsValidationErrors() throws Exception {
        mockMvc.perform(post("/animals")
                        .with(csrf())
                        .param("name", "")
                        .param("type", "CAT")
                        .param("breed", "Siamese")
                        .param("age", "-1")
                        .param("description", "Friendly and calm")
                        .param("imageUrl", "Luna.jpeg"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Name is required")))
                .andExpect(content().string(containsString("Age cannot be negative")));
    }
}
