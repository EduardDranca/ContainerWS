package com.eddranca.containerws.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class GithubConfig {
    companion object {
        const val BASE_URL = "https://api.github.com"
    }
    @Bean("github-web-client")
    fun githubWebClient(): RestClient {
        return RestClient.create(BASE_URL)

    }
}
