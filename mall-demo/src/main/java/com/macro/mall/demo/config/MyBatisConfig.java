package com.macro.mall.demo.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis相关配置
 * fisher
 */
@Configuration
@MapperScan("com.macro.mall.mapper")
public class MyBatisConfig {
}
