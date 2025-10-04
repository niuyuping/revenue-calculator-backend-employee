package jp.asatex.revenue_calculator_backend_employee.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 多语言OpenAPI配置类
 * 根据请求的语言动态生成API文档
 */
@Configuration
public class MultiLanguageOpenApiConfig {

    @Autowired
    private MessageSource messageSource;

    /**
     * 创建多语言OpenAPI配置
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // 这里可以根据当前请求的语言动态设置API信息
            // 由于OpenAPI是全局配置，我们使用默认的英文配置
            // 实际的多语言支持通过Swagger UI的语言切换功能实现
        };
    }

    /**
     * 创建英文版API文档
     */
    @Bean
    public GroupedOpenApi englishApi() {
        return GroupedOpenApi.builder()
                .group("english")
                .displayName("English API Documentation")
                .pathsToMatch("/api/v1/employee/**")
                .addOpenApiCustomizer(createOpenApiCustomizer(Locale.ENGLISH))
                .build();
    }

    /**
     * 创建中文版API文档
     */
    @Bean
    public GroupedOpenApi chineseApi() {
        return GroupedOpenApi.builder()
                .group("chinese")
                .displayName("中文API文档")
                .pathsToMatch("/api/v1/employee/**")
                .addOpenApiCustomizer(createOpenApiCustomizer(Locale.SIMPLIFIED_CHINESE))
                .build();
    }

    /**
     * 创建日文版API文档
     */
    @Bean
    public GroupedOpenApi japaneseApi() {
        return GroupedOpenApi.builder()
                .group("japanese")
                .displayName("日本語API文書")
                .pathsToMatch("/api/v1/employee/**")
                .addOpenApiCustomizer(createOpenApiCustomizer(Locale.JAPANESE))
                .build();
    }

    /**
     * 创建指定语言的OpenAPI自定义器
     */
    private OpenApiCustomizer createOpenApiCustomizer(Locale locale) {
        return openApi -> {
            Info info = new Info()
                    .title(getMessage("app.title", locale))
                    .description(buildDescription(locale))
                    .version("1.0.0")
                    .contact(new Contact()
                            .name(getMessage("contact.name", locale))
                            .email(getMessage("contact.email", locale))
                            .url(getMessage("contact.url", locale)))
                    .license(new License()
                            .name(getMessage("license.name", locale))
                            .url(getMessage("license.url", locale)));

            List<Server> servers = Arrays.asList(
                    new Server()
                            .url("http://localhost:9001")
                            .description(getMessage("server.dev", locale)),
                    new Server()
                            .url("https://api.asatex.jp")
                            .description(getMessage("server.prod", locale))
            );

            openApi.setInfo(info);
            openApi.setServers(servers);
        };
    }

    /**
     * 构建多语言描述
     */
    private String buildDescription(Locale locale) {
        return getMessage("app.description", locale) + "\n\n" +
                "## " + getMessage("features.title", locale) + "\n" +
                "- " + getMessage("features.crud", locale) + "\n" +
                "- " + getMessage("features.search", locale) + "\n" +
                "- " + getMessage("features.reactive", locale) + "\n" +
                "- " + getMessage("features.cache", locale) + "\n" +
                "- " + getMessage("features.ratelimit", locale) + "\n" +
                "- " + getMessage("features.monitoring", locale) + "\n\n" +
                "## " + getMessage("auth.title", locale) + "\n" +
                getMessage("auth.note", locale) + "\n\n" +
                "## " + getMessage("ratelimit.title", locale) + "\n" +
                "- " + getMessage("ratelimit.general", locale) + "\n" +
                "- " + getMessage("ratelimit.search", locale) + "\n" +
                "- " + getMessage("ratelimit.write", locale);
    }

    /**
     * 获取国际化消息
     */
    private String getMessage(String key, Locale locale) {
        try {
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            // 如果获取失败，返回默认的英文消息
            return messageSource.getMessage(key, null, Locale.ENGLISH);
        }
    }
}
