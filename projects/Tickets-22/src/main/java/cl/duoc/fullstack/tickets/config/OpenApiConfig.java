package cl.duoc.fullstack.tickets.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI ticketsOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Tickets API")
            .description("API REST educativa para gestion de tickets de soporte DSY1103")
            .version("1.0.0")
            .license(new License().name("Uso educativo DSY1103")))
        .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
        .components(new Components()
            .addSecuritySchemes("basicAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic")));
  }
}
