package com.bluehospital.patient.patient.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.data.redis.connection.RedisConnectionFactory;


@Configuration
public class RedisConfig {

//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(){
//        RedisStandaloneConfiguration config=new RedisStandaloneConfiguration();
//        config.setHostName("localhost");
//        config.setPort(6379);
//
//        return new LettuceConnectionFactory(config);
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){

        RedisTemplate<String, Object> redisTemplate= new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        //use String Serializer for keys
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Use GenericJackson2JsonRedisSerializer for values
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }


}
