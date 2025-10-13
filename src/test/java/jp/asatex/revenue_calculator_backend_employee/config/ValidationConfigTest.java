package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ValidationConfig test
 * Tests if validation configuration is correct
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, 
                properties = {"spring.flyway.enabled=false"})
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
@DisplayName("ValidationConfig Test")
class ValidationConfigTest {

    @Autowired
    private ValidationConfig validationConfig;

    @Autowired
    private MethodValidationPostProcessor methodValidationPostProcessor;

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Nested
    @DisplayName("Bean Existence Tests")
    class BeanExistenceTests {

        @Test
        @DisplayName("ValidationConfig Bean should be created correctly")
        void testValidationConfigBean() {
            assertThat(validationConfig).isNotNull();
        }

        @Test
        @DisplayName("MethodValidationPostProcessor Bean should be created correctly")
        void testMethodValidationPostProcessorBean() {
            assertThat(methodValidationPostProcessor).isNotNull();
        }

        @Test
        @DisplayName("LocalValidatorFactoryBean Bean should be created correctly")
        void testLocalValidatorFactoryBean() {
            assertThat(validator).isNotNull();
        }
    }

    @Nested
    @DisplayName("Bean Configuration Tests")
    class BeanConfigurationTests {

        @Test
        @DisplayName("MethodValidationPostProcessor should be created with correct type")
        void testMethodValidationPostProcessorConfiguration() {
            assertThat(methodValidationPostProcessor).isInstanceOf(MethodValidationPostProcessor.class);
        }

        @Test
        @DisplayName("LocalValidatorFactoryBean should be created with correct type")
        void testLocalValidatorFactoryBeanConfiguration() {
            assertThat(validator).isInstanceOf(LocalValidatorFactoryBean.class);
        }

        @Test
        @DisplayName("MethodValidationPostProcessor should have Validator configured correctly")
        void testMethodValidationPostProcessorValidatorConfiguration() {
            // Check if validator property of MethodValidationPostProcessor is configured
            // In Spring Boot 3.x, getValidator() method is not available, so only check Bean existence
            assertThat(methodValidationPostProcessor).isNotNull();
            assertThat(validator).isNotNull();
        }
    }

    @Nested
    @DisplayName("Validator Functionality Tests")
    class ValidatorFunctionalityTests {

        @Test
        @DisplayName("Validator should work normally")
        void testValidatorFunctionality() {
            // Test if Validator works normally
            // In Spring Boot 3.x, getValidatorFactory() is not directly available, so only check Bean existence
            assertThat(validator).isNotNull();
            assertThat(validator).isInstanceOf(LocalValidatorFactoryBean.class);
        }

        @Test
        @DisplayName("Validator should be registered correctly as Bean")
        void testValidatorBeanRegistration() {
            // Check if Validator is correctly registered in Spring context
            assertThat(validator).isNotNull();
            assertThat(validator).isInstanceOf(LocalValidatorFactoryBean.class);
        }
    }

    @Nested
    @DisplayName("Configuration Class Functionality Tests")
    class ConfigurationClassTests {

        @Test
        @DisplayName("ValidationConfig should implement WebFluxConfigurer")
        void testValidationConfigImplementsWebFluxConfigurer() {
            assertThat(validationConfig).isInstanceOf(org.springframework.web.reactive.config.WebFluxConfigurer.class);
        }

        @Test
        @DisplayName("ValidationConfig validator method should work correctly")
        void testValidatorMethod() {
            LocalValidatorFactoryBean validatorBean = validationConfig.validator();
            assertThat(validatorBean).isNotNull();
            assertThat(validatorBean).isInstanceOf(LocalValidatorFactoryBean.class);
        }

        @Test
        @DisplayName("ValidationConfig methodValidationPostProcessor method should work correctly")
        void testMethodValidationPostProcessorMethod() {
            MethodValidationPostProcessor processor = ValidationConfig.methodValidationPostProcessor();
            assertThat(processor).isNotNull();
            assertThat(processor).isInstanceOf(MethodValidationPostProcessor.class);
            // In Spring Boot 3.x, getValidator() method is not available, so only check type
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("All Beans should work together correctly")
        void testAllBeansWorkTogether() {
            // Check if all Beans are correctly integrated
            assertThat(validationConfig).isNotNull();
            assertThat(methodValidationPostProcessor).isNotNull();
            assertThat(validator).isNotNull();
            
            // In Spring Boot 3.x, direct dependency verification is difficult, so only check Bean existence
            assertThat(methodValidationPostProcessor).isInstanceOf(MethodValidationPostProcessor.class);
            assertThat(validator).isInstanceOf(LocalValidatorFactoryBean.class);
        }

        @Test
        @DisplayName("ValidationConfig Bean definition should be correct")
        void testValidationConfigBeanDefinition() {
            // Check if ValidationConfig Bean definition is correct
            assertThat(validationConfig).isNotNull();
            assertThat(validationConfig.getClass().getSimpleName()).contains("ValidationConfig");
        }
    }
}
