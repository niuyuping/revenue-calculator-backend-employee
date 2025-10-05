package jp.asatex.revenue_calculator_backend_employee.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux Jackson配置类
 * 通过WebFluxConfigurer强制配置编解码器以支持Java 8时间类型
 */
@Configuration
public class WebFluxJacksonConfig implements WebFluxConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebFluxJacksonConfig.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        logger.info("=== WebFluxJacksonConfig.configureHttpMessageCodecs() 开始执行 ===");
        logger.info("使用统一的ObjectMapper实例（已配置JSR310支持）");
        
        // 配置编码器（响应序列化）
        configurer.defaultCodecs().jackson2JsonEncoder(new org.springframework.http.codec.json.Jackson2JsonEncoder(objectMapper));
        logger.info("配置Jackson2JsonEncoder");
        
        // 配置解码器（请求反序列化）
        configurer.defaultCodecs().jackson2JsonDecoder(new org.springframework.http.codec.json.Jackson2JsonDecoder(objectMapper));
        logger.info("配置Jackson2JsonDecoder");
        
        logger.info("=== WebFluxJacksonConfig.configureHttpMessageCodecs() 执行完成 ===");
    }
}
