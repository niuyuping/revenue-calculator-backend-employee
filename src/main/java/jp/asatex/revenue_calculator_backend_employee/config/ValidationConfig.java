package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Validation configuration class
 * Configures WebFlux parameter validation
 */
@Configuration
public class ValidationConfig implements WebFluxConfigurer {

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public static MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(new LocalValidatorFactoryBean());
        return processor;
    }
}
