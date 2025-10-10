package jp.asatex.revenue_calculator_backend_employee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Swagger/OpenAPI configuration class
 * Configures API documentation and Swagger UI settings
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

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
                .servers(buildServers());
    }

    /**
     * Build server list based on active profile
     */
    private List<Server> buildServers() {
        if ("prod".equals(activeProfile)) {
            // Production environment - auto-detect from current request
            return List.of(
                    new Server()
                            .url("/")
                            .description("Production server")
            );
        } else {
            // Development environment
            return List.of(
                    new Server()
                            .url("http://localhost:" + serverPort)
                            .description("Development server")
            );
        }
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
                "- Rate limiting for API protection\n\n" +
                "## Authentication\n" +
                "Currently no authentication required. All endpoints are publicly accessible.\n\n" +
                "## Rate Limiting\n" +
                "- General API calls: 100 requests per minute\n" +
                "- Search operations: 50 requests per minute\n" +
                "- Write operations: 20 requests per minute\n\n" +
                "## API Groups\n" +
                "- **Employee Management**: Core business operations\n" +
                "- **System Monitoring**: Internal monitoring endpoints (hidden by default)\n\n" +
                "## Documentation\n" +
                "This API documentation is generated automatically from the source code annotations.";
    }

    @Bean
    public GroupedOpenApi employeeApi() {
        return GroupedOpenApi.builder()
                .group("employee-management")
                .pathsToMatch("/api/v1/employee/**")
                .displayName("Employee Management API")
                .build();
    }

    @Bean
    @Profile("!prod")
    public GroupedOpenApi monitoringApi() {
        return GroupedOpenApi.builder()
                .group("system-monitoring")
                .pathsToMatch("/api/v1/monitoring/**", "/api/v1/audit/**")
                .displayName("System Monitoring API")
                .build();
    }

    @Bean
    @Profile("!prod")
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all-apis")
                .pathsToMatch("/api/**")
                .displayName("All APIs")
                .build();
    }

}
