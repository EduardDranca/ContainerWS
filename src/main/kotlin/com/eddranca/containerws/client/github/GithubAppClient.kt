package com.eddranca.containerws.client.github

import com.eddranca.containerws.client.github.exceptions.ResetTokenException
import com.eddranca.containerws.client.github.model.ResetTokenRequest
import com.eddranca.containerws.client.github.model.ResetTokenResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import org.springframework.web.reactive.function.client.bodyToMono
import java.util.*

@Component
class GithubAppClient(@Value("\${spring.security.oauth2.client.registration.github.clientId}") private val clientId: String,
                      @Value("\${spring.security.oauth2.client.registration.github.clientSecret}") private val clientSecret: String,
                      @Qualifier("github-web-client") private val webClient: WebClient) {
    private final val basicAuth: String = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

    companion object {
        const val ACCEPT_HEADER = "application/vnd.github+json"
    }

    fun resetOauthToken(resetTokenRequest: ResetTokenRequest): String {
        val resetTokenResponse = addHeaders(this.webClient.patch()
            .uri("/applications/{clientId}/token", clientId)
            .bodyValue(resetTokenRequest))
            .retrieve()
            .bodyToMono<ResetTokenResponse>()
            .block()
        return resetTokenResponse?.token ?: throw ResetTokenException("GHO Token reset failed. Token might be invalid!")
    }

    private fun addHeaders(request: RequestHeadersSpec<*>): RequestHeadersSpec<*> {
        return request.header("Accept", ACCEPT_HEADER)
            .header("Authorization", "Basic $basicAuth")
    }
}
