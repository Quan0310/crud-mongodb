package com.example.demo.config.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
@EnableCaching
public class RedisCacheConfig {
	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;

	
	@Bean
	public LettuceConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);

		return new LettuceConnectionFactory(configuration);
	}

	private RedisCacheConfiguration myDefaultCacheConfig(Duration duration) {
		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(duration)
				.serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
	}

	@Bean
	public RedisCacheManager cacheManager() {
		RedisCacheConfiguration cacheConfig = myDefaultCacheConfig(Duration.ofSeconds(60)).disableCachingNullValues();

		return RedisCacheManager.builder(redisConnectionFactory()).cacheDefaults(cacheConfig)
//				.withCacheConfiguration("person", myDefaultCacheConfig(Duration.ofSeconds(60)))
//				.withCacheConfiguration("persons", myDefaultCacheConfig(Duration.ofSeconds(60)))
				.build();
	}

	
}