package lv.bootcamp.shelter.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecuritySchemes({
        @SecurityScheme(
                name = "basicAuth",
                type = SecuritySchemeType.HTTP,
                scheme = "basic"
        ),
        @SecurityScheme(
                name = "bearerAuth",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT"
        )
})
public class OpenApiConfig {

    @Bean
    public OpenAPI shelterOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shelter API")
                        .version("1.0")
                        .description("Versioned REST API for browsing, creating, and adopting shelter animals.")
                        .contact(new Contact()
                                .name("Shelter Bootcamp Team")
                                .email("shelter@example.com")
                                .url("https://example.com/shelter"))
                        .license(new License()
                                .name("Educational use")
                                .url("https://example.com/license")))
                .externalDocs(new ExternalDocumentation()
                        .description("Spring Boot reference documentation")
                        .url("https://docs.spring.io/spring-boot/index.html"));
    }
}
