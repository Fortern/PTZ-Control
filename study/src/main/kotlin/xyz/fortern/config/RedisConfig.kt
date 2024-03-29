package xyz.fortern.config

import com.alibaba.fastjson2.support.spring.data.redis.GenericFastJsonRedisSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * Redis配置类
 */
@Configuration
open class RedisConfig {
	@Bean
	open fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
		//Redis模板对象
		val template = RedisTemplate<String, Any>()
		
		//设置连接工厂
		template.setConnectionFactory(redisConnectionFactory)
		
		//设置自定义序列化方式
		//key：字符串类型，使用String的序列化方式
		val stringRedisSerializer = StringRedisSerializer()
		
		//value：Object类型，使用fastjson的序列化方式,直接序列化对象
		val fastJsonRedisSerializer = GenericFastJsonRedisSerializer()
		
		//指定序列化和反序列化方式
		template.keySerializer = stringRedisSerializer
		template.valueSerializer = fastJsonRedisSerializer
		template.hashKeySerializer = stringRedisSerializer
		template.hashValueSerializer = fastJsonRedisSerializer
		
		//初始化模板
		template.afterPropertiesSet()
		return template
	}
	
	//连接到Redis哨兵集群的配置
//	@Bean
//	open fun clientConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer {
//		return LettuceClientConfigurationBuilderCustomizer { clientConfigurationBuilder: LettuceClientConfigurationBuilder ->
//			clientConfigurationBuilder.readFrom(
//				ReadFrom.REPLICA_PREFERRED
//			)
//		}
//	}
}
