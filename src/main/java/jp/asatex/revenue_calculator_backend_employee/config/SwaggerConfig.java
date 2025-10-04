package jp.asatex.revenue_calculator_backend_employee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Locale;

/**
 * Swagger/OpenAPI設定クラス
 * API文書の設定とSwagger UIの設定を行う
 * 支持英、中、日三种语言的API文档
 */
@Configuration
public class SwaggerConfig {

    @Autowired
    private MessageSource messageSource;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(getMessage("app.title", Locale.ENGLISH))
                        .description(buildDescription(Locale.ENGLISH))
                        .version("1.0.0")
                        .contact(new Contact()
                                .name(getMessage("contact.name", Locale.ENGLISH))
                                .email(getMessage("contact.email", Locale.ENGLISH))
                                .url(getMessage("contact.url", Locale.ENGLISH)))
                        .license(new License()
                                .name(getMessage("license.name", Locale.ENGLISH))
                                .url(getMessage("license.url", Locale.ENGLISH))))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:9001")
                                .description(getMessage("server.dev", Locale.ENGLISH)),
                        new Server()
                                .url("https://api.asatex.jp")
                                .description(getMessage("server.prod", Locale.ENGLISH))
                ));
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

    @Bean
    public GroupedOpenApi employeeApi() {
        return GroupedOpenApi.builder()
                .group(getMessage("api.group.employee", Locale.ENGLISH))
                .pathsToMatch("/api/v1/employee/**")
                .build();
    }

}
