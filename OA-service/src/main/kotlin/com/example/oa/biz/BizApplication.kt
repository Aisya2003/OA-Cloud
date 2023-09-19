package com.example.oa.biz

import com.example.oa.config.LocalDateTimeConfig
import com.example.oa.config.MybatisPlusConfig
import com.example.oa.biz.config.CustomSecurityConfig
import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@MapperScan("com.example.oa.biz.mapper")
@Import(MybatisPlusConfig::class, LocalDateTimeConfig::class, CustomSecurityConfig::class)
class BizApplication

fun main(args: Array<String>) {
    runApplication<BizApplication>(*args)
}
