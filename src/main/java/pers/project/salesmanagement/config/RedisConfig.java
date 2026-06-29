package pers.project.salesmanagement.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

        @Value("${app.cache.ttl:3600}")
        private long cacheTtl;

        @Bean
        public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
                ObjectMapper objectMapper = new ObjectMapper();

                // Register Spring Data modules (Page & Slice support)
                objectMapper.registerModule(new SpringDataJacksonConfiguration.PageModule(
                                new org.springframework.data.web.config.SpringDataWebSettings(
                                                org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.DIRECT)));

                // Enable default typing for polymorphic/record type deserialization
                objectMapper.activateDefaultTyping(
                                LaissezFaireSubTypeValidator.instance,
                                ObjectMapper.DefaultTyping.NON_FINAL,
                                JsonTypeInfo.As.PROPERTY);

                GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

                RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofSeconds(cacheTtl))
                                .disableCachingNullValues()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair
                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair.fromSerializer(serializer));

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(config)
                                .build();
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new SpringDataJacksonConfiguration.PageModule(
                                new org.springframework.data.web.config.SpringDataWebSettings(
                                                org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.DIRECT)));
                objectMapper.activateDefaultTyping(
                                LaissezFaireSubTypeValidator.instance,
                                ObjectMapper.DefaultTyping.NON_FINAL,
                                JsonTypeInfo.As.PROPERTY);

                GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

                template.setKeySerializer(new StringRedisSerializer());
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(serializer);
                template.setHashValueSerializer(serializer);

                return template;
        }
}
