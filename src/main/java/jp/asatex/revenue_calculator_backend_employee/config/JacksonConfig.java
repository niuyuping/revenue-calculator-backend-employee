package jp.asatex.revenue_calculator_backend_employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Unified Jackson configuration class
 * Centralized management of all JSON serialization configurations to ensure consistency
 */
@Configuration
public class JacksonConfig {

    /**
     * Create unified ObjectMapper Bean
     * Supports Java 8 time types for all JSON serialization scenarios
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JSR-310 module to support Java 8 time types
        mapper.registerModule(new JavaTimeModule());
        
        // Disable timestamp format, use ISO-8601 format
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Ignore unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Allow empty Bean serialization
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        return mapper;
    }

}
