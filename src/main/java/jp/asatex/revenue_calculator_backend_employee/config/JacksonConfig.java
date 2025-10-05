package jp.asatex.revenue_calculator_backend_employee.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * 统一的Jackson配置类
 * 集中管理所有JSON序列化配置，确保一致性
 */
@Configuration
public class JacksonConfig {

    /**
     * 创建统一的ObjectMapper Bean
     * 支持Java 8时间类型，用于所有JSON序列化场景
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 注册JSR-310模块以支持Java 8时间类型
        mapper.registerModule(new JavaTimeModule());
        
        // 禁用时间戳格式，使用ISO-8601格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 忽略未知属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 允许空Bean序列化
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        return mapper;
    }

    /**
     * 创建支持JSR310的Redis序列化器
     * 使用统一的ObjectMapper确保缓存序列化的一致性
     * 配置类型信息以支持正确的反序列化
     */
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        // 创建专门用于Redis的ObjectMapper
        ObjectMapper redisObjectMapper = objectMapper.copy();
        
        // 启用类型信息以支持正确的反序列化
        redisObjectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }
}
