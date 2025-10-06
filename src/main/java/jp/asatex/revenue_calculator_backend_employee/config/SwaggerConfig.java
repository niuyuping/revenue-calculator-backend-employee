package jp.asatex.revenue_calculator_backend_employee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration class
 * Configures API documentation and Swagger UI settings
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Revenue Calculator Employee API")
                        .description(buildDescription())
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("ASATEX Development Team")
                                .email("dev@asatex.jp")
                                .url("https://asatex.jp"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9001")
                                .description("Development server"),
                        new Server()
                                .url("https://api.asatex.jp")
                                .description("Production server")
                ));
    }

    /**
     * Build API description
     */
    private String buildDescription() {
        return "Employee management API for Revenue Calculator system.\n\n" +
                "## Features\n" +
                "- Complete CRUD operations for employee data\n" +
                "- Advanced search functionality (by name and furigana)\n" +
                "- Reactive programming with Spring WebFlux\n" +
                "- Redis caching for performance optimization\n" +
                "- Rate limiting for API protection\n" +
                "- Comprehensive monitoring and audit logging\n\n" +
                "## Authentication\n" +
                "Currently no authentication required. All endpoints are publicly accessible.\n\n" +
                "## Rate Limiting\n" +
                "- General API calls: 100 requests per minute\n" +
                "- Search operations: 50 requests per minute\n" +
                "- Write operations: 20 requests per minute";
    }

    @Bean
    public GroupedOpenApi employeeApi() {
        return GroupedOpenApi.builder()
                .group("Employee Management")
                .pathsToMatch("/api/v1/employee/**")
                .build();
    }

}
