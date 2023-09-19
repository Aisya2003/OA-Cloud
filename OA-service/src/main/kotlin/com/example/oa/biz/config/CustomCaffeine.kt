package com.example.oa.biz.config

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.GrantedAuthority
import java.time.Duration

@Configuration
class CustomCaffeine {
    //存储认证信息
    @Bean
    fun caffeineCache(): Cache<Long, List<GrantedAuthority>> = Caffeine.newBuilder()
        .maximumSize(100)
        //初始存放时间
        .expireAfterWrite(Duration.ofMinutes(30))
        //访问后存放时间
        .expireAfterAccess(Duration.ofHours(1))
        .build()
}