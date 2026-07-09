package lv.bootcamp.shelter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
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
                .andExpect(content().string(containsString("Adopt animal")))
                .andExpect(content().string(containsString("Back to list")));

        mockMvc.perform(get("/animals/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void customLoginPageIsPublicAndShowsErrorMessage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Shelter staff login")))
                .andExpect(content().string(containsString("name=\"username\"")))
                .andExpect(content().string(containsString("name=\"password\"")));

        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid username or password")));
    }

    @Test
    void loginRedirectsToAnimalsAfterSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animals"));
    }

    @Test
    void addAnimalFormRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/animals/new").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void animalTypesApiReturnsAvailableTypes() throws Exception {
        mockMvc.perform(get("/api/v1/animals/types"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CAT")))
                .andExpect(content().string(containsString("DOG")))
                .andExpect(content().string(containsString("OTHER")));
    }

    @Test
    void swaggerUiAndOpenApiDocsArePublic() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/v1/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Shelter API")))
                .andExpect(content().string(containsString("Create animal")))
                .andExpect(content().string(containsString("/api/v1/animals")))
                .andExpect(content().string(containsString("basicAuth")))
                .andExpect(content().string(containsString("bearerAuth")))
                .andExpect(content().string(containsString("Spring Boot reference documentation")));
    }

    @Test
    void adminCanCreateAnimalThroughApi() throws Exception {
        mockMvc.perform(post("/api/v1/animals")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "SwaggerCat",
                                  "type": "CAT",
                                  "breed": "Tabby",
                                  "age": 2,
                                  "description": "Created through the documented API",
                                  "imageUrl": "Luna.jpeg"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("SwaggerCat")));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void userCanAdoptAnimalFromDetailPage() throws Exception {
        mockMvc.perform(post("/animals/6/adopt").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/animals"))
                .andExpect(flash().attribute("message", "Animal adopted!"));

        mockMvc.perform(get("/animals/6"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pepper")))
                .andExpect(content().string(containsString("ADOPTED")))
                .andExpect(content().string(not(containsString("Adopt animal"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanOpenAndSubmitAddAnimalForm() throws Exception {
        mockMvc.perform(get("/animals/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add a new animal")))
                .andExpect(content().string(containsString("fetch('/api/v1/animals/types')")));

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
