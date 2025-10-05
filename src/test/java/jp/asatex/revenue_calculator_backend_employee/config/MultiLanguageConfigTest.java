package jp.asatex.revenue_calculator_backend_employee.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.TestPropertySource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 多语言配置测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {"spring.flyway.enabled=false"})
@TestPropertySource(properties = {
    "spring.messages.basename=messages",
    "spring.messages.encoding=UTF-8"
})
class MultiLanguageConfigTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    void testMessageSourceExists() {
        assertNotNull(messageSource, "MessageSource should be injected");
    }

    @Test
    void testEnglishMessages() {
        // 测试英文消息
        String title = messageSource.getMessage("app.title", null, Locale.ENGLISH);
        assertNotNull(title, "English title should not be null");
        assertTrue(title.contains("API") || title.contains("收入计算器"), 
            "Title should contain API or Chinese equivalent: " + title);
        
        String description = messageSource.getMessage("app.description", null, Locale.ENGLISH);
        assertNotNull(description, "English description should not be null");
    }

    @Test
    void testChineseMessages() {
        String title = messageSource.getMessage("app.title", null, Locale.SIMPLIFIED_CHINESE);
        assertNotNull(title, "Chinese title should not be null");
        assertTrue(title.contains("收入计算器") || title.contains("API"), 
            "Title should contain Chinese or English: " + title);
        
        String description = messageSource.getMessage("app.description", null, Locale.SIMPLIFIED_CHINESE);
        assertNotNull(description, "Chinese description should not be null");
    }

    @Test
    void testJapaneseMessages() {
        String title = messageSource.getMessage("app.title", null, Locale.JAPANESE);
        assertNotNull(title, "Japanese title should not be null");
        
        String description = messageSource.getMessage("app.description", null, Locale.JAPANESE);
        assertNotNull(description, "Japanese description should not be null");
    }

    @Test
    void testDefaultLocale() {
        // 测试默认语言
        String title = messageSource.getMessage("app.title", null, null);
        assertNotNull(title, "Default locale title should not be null");
    }

    @Test
    void testFeaturesMessages() {
        // 测试功能特性消息
        String featuresTitle = messageSource.getMessage("features.title", null, Locale.ENGLISH);
        assertNotNull(featuresTitle, "Features title should not be null");
        
        String crudFeature = messageSource.getMessage("features.crud", null, Locale.ENGLISH);
        assertNotNull(crudFeature, "CRUD feature should not be null");
    }
}
