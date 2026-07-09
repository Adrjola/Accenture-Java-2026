package lv.bootcamp.shelter.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI shelterOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shelter API")
                        .version("1.0")
                        .description("REST API for browsing, creating, and adopting shelter animals."));
    }
}
