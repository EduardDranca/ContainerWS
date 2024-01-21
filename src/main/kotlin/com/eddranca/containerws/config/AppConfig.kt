package com.eddranca.containerws.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AppConfig {
    @Bean
    fun processBuilder(): ProcessBuilder {
        return ProcessBuilder()
    }
}
