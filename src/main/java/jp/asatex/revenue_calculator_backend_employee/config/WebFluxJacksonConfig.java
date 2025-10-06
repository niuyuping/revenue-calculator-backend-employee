package jp.asatex.revenue_calculator_backend_employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux Jackson configuration class
 * Forces configuration of codecs through WebFluxConfigurer to support Java 8 time types
 */
@Configuration
public class WebFluxJacksonConfig implements WebFluxConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebFluxJacksonConfig.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer configurer) {
        logger.info("=== WebFluxJacksonConfig.configureHttpMessageCodecs() started ===");
        logger.info("Using unified ObjectMapper instance (configured with JSR310 support)");
        
        // Configure encoder (response serialization)
        configurer.defaultCodecs().jackson2JsonEncoder(new org.springframework.http.codec.json.Jackson2JsonEncoder(objectMapper));
        logger.info("Configured Jackson2JsonEncoder");
        
        // Configure decoder (request deserialization)
        configurer.defaultCodecs().jackson2JsonDecoder(new org.springframework.http.codec.json.Jackson2JsonDecoder(objectMapper));
        logger.info("Configured Jackson2JsonDecoder");
        
        logger.info("=== WebFluxJacksonConfig.configureHttpMessageCodecs() completed ===");
    }
}
