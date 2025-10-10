package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux configuration class
 * Configures static resource handling and other web-related settings
 */
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure static resource handling
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // Configure favicon handling
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/");
        
        // Configure Swagger UI resources
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
        
        // Configure API docs
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/");
    }
}
