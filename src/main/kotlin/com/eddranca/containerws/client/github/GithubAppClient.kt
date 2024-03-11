package com.eddranca.containerws.client.github

import com.eddranca.containerws.client.github.exceptions.ResetTokenException
import com.eddranca.containerws.client.github.model.CheckTokenRequest
import com.eddranca.containerws.client.github.model.ResetTokenRequest
import com.eddranca.containerws.client.github.model.ResetTokenResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestHeadersSpec
import java.util.*

//TODO: logging
@Component
class GithubAppClient(@Value("\${spring.security.oauth2.client.registration.github.clientId}") private val clientId: String,
                      @Value("\${spring.security.oauth2.client.registration.github.clientSecret}") private val clientSecret: String,
                      @Qualifier("github-web-client") private val restClient: RestClient) {
    private final val basicAuth: String = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

    companion object {
        const val ACCEPT_HEADER = "application/vnd.github+json"
        const val GITHUB_TOKEN_API_ENDPOINT = "/applications/{clientId}/token"
    }

    fun resetOauthToken(token: String): String {
        val newToken = try {
            val resetTokenResponse = addHeaders(this.restClient.patch()
                    .uri(GITHUB_TOKEN_API_ENDPOINT, clientId)
                    .body(ResetTokenRequest(token)))
                    .retrieve()
                    .body(ResetTokenResponse::class.java)
            resetTokenResponse?.token
        } catch (e: Exception) {
            throw ResetTokenException("Exception thrown while resetting user GHO token. Token might be invalid!", e)
        }
        if (newToken == null) {
            throw ResetTokenException("GHO Token reset failed. Token might be invalid!")
        }
        return newToken
    }

    fun isTokenValid(token: String): Boolean {
        try {
            addHeaders(this.restClient.post()
                    .uri(GITHUB_TOKEN_API_ENDPOINT, clientId)
                    .body(CheckTokenRequest(token)))
                    .retrieve()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun addHeaders(request: RequestHeadersSpec<*>): RequestHeadersSpec<*> {
        return request.header(HttpHeaders.ACCEPT, ACCEPT_HEADER)
            .header(HttpHeaders.AUTHORIZATION, "Basic $basicAuth")
    }
}
