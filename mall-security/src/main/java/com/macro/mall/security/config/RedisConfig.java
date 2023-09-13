package com.macro.mall.security.config;

import com.macro.mall.common.config.BaseRedisConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Redis配置类
 * fisher
 */
@EnableCaching
@Configuration
public class RedisConfig extends BaseRedisConfig {

}
