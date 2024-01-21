package com.eddranca.containerws.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class GithubConfig {
    companion object {
        const val BASE_URL = "https://api.github.com"
    }
    @Bean("github-web-client")
    fun githubWebClient(): WebClient {
        return WebClient.create(BASE_URL)
    }
}
