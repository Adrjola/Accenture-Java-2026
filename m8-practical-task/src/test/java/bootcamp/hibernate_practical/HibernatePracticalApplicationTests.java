package bootcamp.hibernate_practical;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HibernatePracticalApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
    }

    @Test
    void getBooksRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCanGetBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                        .with(httpBasic("user", "user")))
                .andExpect(status().isOk());
    }

    @Test
    void userCannotCreateBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(httpBasic("user", "user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code",
                                  "author": "Robert Martin",
                                  "genre": "Programming",
                                  "publicationYear": 2008
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code",
                                  "author": "Robert Martin",
                                  "genre": "Programming",
                                  "publicationYear": 2008
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void userCannotUpdateBook() throws Exception {
        mockMvc.perform(put("/api/books/1")
                        .with(httpBasic("user", "user"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Architecture",
                                  "author": "Robert Martin",
                                  "genre": "Programming",
                                  "publicationYear": 2017,
                                  "available": false
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateBook() throws Exception {
        long id = createBookAndReturnId();

        mockMvc.perform(put("/api/books/" + id)
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Architecture",
                                  "author": "Robert Martin",
                                  "genre": "Programming",
                                  "publicationYear": 2017,
                                  "available": false
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanDeleteBook() throws Exception {
        long id = createBookAndReturnId();

        mockMvc.perform(delete("/api/books/" + id)
                        .with(httpBasic("admin", "admin")))
                .andExpect(status().isNoContent());
    }

    private long createBookAndReturnId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/books")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Test Book",
                                  "author": "Test Author",
                                  "genre": "Testing",
                                  "publicationYear": 2024
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode book = objectMapper.readTree(result.getResponse().getContentAsString());
        return book.get("id").asLong();
    }
}
