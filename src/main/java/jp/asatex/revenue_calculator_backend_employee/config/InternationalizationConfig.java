package jp.asatex.revenue_calculator_backend_employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 国际化配置类
 * 支持英、中、日三种语言的API文档
 */
@Configuration
public class InternationalizationConfig implements WebFluxConfigurer {

    /**
     * 配置语言解析器
     * 支持通过Accept-Language头或查询参数切换语言
     */
    @Bean
    public LocaleContextResolver localeContextResolver() {
        AcceptHeaderLocaleContextResolver resolver = new AcceptHeaderLocaleContextResolver();
        
        // 设置支持的语言
        List<Locale> supportedLocales = Arrays.asList(
            Locale.ENGLISH,      // en
            Locale.SIMPLIFIED_CHINESE,  // zh_CN
            Locale.JAPANESE      // ja
        );
        resolver.setSupportedLocales(supportedLocales);
        
        // 设置默认语言为英语
        resolver.setDefaultLocale(Locale.ENGLISH);
        
        return resolver;
    }
}
