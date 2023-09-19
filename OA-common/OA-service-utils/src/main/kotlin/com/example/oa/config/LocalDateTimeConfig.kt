package com.example.oa.config

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class LocalDateTimeConfig {
    @Bean
    fun localDateTimeSerializer(): LocalDateTimeSerializer {
        return LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    @Bean
    fun localDateTimeDeserializer(): LocalDateTimeDeserializer {
        return LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    @Bean
    fun jackson2ObjectMapperBuilderCustomizer(): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer {
            it.serializerByType(LocalDateTime::class.java, localDateTimeSerializer())
            it.deserializerByType(LocalDateTime::class.java, localDateTimeDeserializer())
        }
    }

}