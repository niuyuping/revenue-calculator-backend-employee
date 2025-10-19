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
                        .version("1.4.0")
                        .contact(new Contact()
                                .name("ASATEX Development Team")
                                .email("niuyuping@asatex.jp")
                                .url("https://www.asatex.jp"))
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
        } else if ("test".equals(activeProfile)) {
            // Testing environment
            return List.of(
                    new Server()
                            .url("/test/")
                            .description("Testing server")
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
                "- Complete CRUD operations for employee data (including comprehensive allowance and fee tracking)\n" +
                "- Advanced search functionality (by name and furigana)\n" +
                "- Comprehensive data validation (email format, monetary constraints, rate validation)\n" +
                "- Reactive programming with Spring WebFlux\n" +
                "- Rate limiting for API protection\n\n" +
                "## Employee Data Model\n" +
                "- **Basic Info**: Employee number, name, furigana, birthday\n" +
                "- **Contact**: Email address with format validation\n" +
                "- **Compensation**: Basic salary in JPY with precision handling\n" +
                "- **Insurance**: Health insurance and welfare pension enrollment status\n" +
                "- **Dependents**: Dependent count for tax calculation\n" +
                "- **Pricing**: Unit price per hour/day and individual business amounts\n" +
                "- **Allowances**: Position, housing, and family allowance amounts\n" +
                "- **Fees**: Collection and payment fee amounts\n" +
                "- **Third Party**: Management and profit distribution rates\n" +
                "- **System Fields**: Created/updated timestamps, soft delete support\n\n" +
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
