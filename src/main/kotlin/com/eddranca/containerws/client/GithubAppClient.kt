package com.eddranca.containerws.client

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.bodyToMono
import java.lang.RuntimeException
import java.util.Base64

@Component
class GithubAppClient(@Value("\${spring.security.oauth2.client.registration.github.clientId}") private val clientId: String,
                      @Value("\${spring.security.oauth2.client.registration.github.clientSecret}") private val clientSecret: String,
                      @Qualifier("github-web-client") private val webClient: WebClient) {
    private final val basicAuth: String = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

    companion object {
        const val ACCEPT_HEADER = "application/vnd.github+json"
    }

    fun resetOauthToken(currentToken: String): String {
        return addHeaders(this.webClient.patch()
            .uri("/applications/{clientId}/token", mapOf(Pair("clientId", clientId)))
            .bodyValue(mapOf(Pair("access_token", currentToken))))
            .retrieve()
            .bodyToMono<Map<String, String>>()
            .block()
            ?.get("access_token") ?:
            throw RuntimeException("Could not reset OAuth Token.")
    }

    private fun addHeaders(request: RequestHeadersSpec<*>): RequestHeadersSpec<*> {
        return request.header("Accept", ACCEPT_HEADER)
            .header("Authorization", "Basic $basicAuth")
    }
}
