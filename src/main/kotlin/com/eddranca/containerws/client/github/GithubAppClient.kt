package com.eddranca.containerws.client.github

import com.eddranca.containerws.client.github.exceptions.ResetTokenException
import com.eddranca.containerws.client.github.model.ResetTokenRequest
import com.eddranca.containerws.client.github.model.ResetTokenResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestHeadersSpec
import java.util.*

@Component
class GithubAppClient(@Value("\${spring.security.oauth2.client.registration.github.clientId}") private val clientId: String,
                      @Value("\${spring.security.oauth2.client.registration.github.clientSecret}") private val clientSecret: String,
                      @Qualifier("github-web-client") private val restClient: RestClient) {
    private final val basicAuth: String = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

    companion object {
        const val ACCEPT_HEADER = "application/vnd.github+json"
        const val GITHUB_RESET_TOKEN_URL = "/applications/{clientId}/token"
    }

    fun resetOauthToken(resetTokenRequest: ResetTokenRequest): String {
        try {
            val resetTokenResponse = addHeaders(this.restClient.patch()
                    .uri(GITHUB_RESET_TOKEN_URL, clientId)
                    .body(resetTokenRequest))
                    .retrieve()
                    .body(ResetTokenResponse::class.java)
            return resetTokenResponse?.token ?: throw ResetTokenException(message = "GHO Token reset failed. Token might be invalid!")
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    private fun addHeaders(request: RequestHeadersSpec<*>): RequestHeadersSpec<*> {
        return request.header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
            .header(HttpHeaders.AUTHORIZATION, "Basic $basicAuth")
    }

    private fun handleException(e: Exception): ResetTokenException {
        if (e is ResetTokenException) {
            return e
        }
        return ResetTokenException("GHO Token reset failed. Token might be invalid!", e)
    }
}
