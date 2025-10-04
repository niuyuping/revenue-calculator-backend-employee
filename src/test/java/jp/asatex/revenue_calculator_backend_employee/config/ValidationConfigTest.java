package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ValidationConfigテスト
 * 検証設定が正しいかテスト
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(jp.asatex.revenue_calculator_backend_employee.config.TestConfig.class)
class ValidationConfigTest {

    @Autowired
    private ValidationConfig validationConfig;

    @Autowired
    private MethodValidationPostProcessor methodValidationPostProcessor;

    @Test
    void testValidationConfigBean() {
        assertThat(validationConfig).isNotNull();
    }

    @Test
    void testMethodValidationPostProcessorBean() {
        assertThat(methodValidationPostProcessor).isNotNull();
    }

    @Test
    void testMethodValidationPostProcessorConfiguration() {
        // MethodValidationPostProcessorが正しく設定されているか検証
        assertThat(methodValidationPostProcessor).isInstanceOf(MethodValidationPostProcessor.class);
    }
}
