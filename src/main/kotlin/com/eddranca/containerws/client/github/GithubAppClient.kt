package com.eddranca.containerws.client.github

import com.eddranca.containerws.client.github.exceptions.ResetTokenException
import com.eddranca.containerws.client.github.model.CheckTokenRequest
import com.eddranca.containerws.client.github.model.ResetTokenRequest
import com.eddranca.containerws.client.github.model.ResetTokenResponse
import com.eddranca.containerws.client.github.model.RevokeTokenRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestHeadersSpec
import java.util.*

//TODO: logging
@Component
class GithubAppClient(@Value("\${spring.security.oauth2.client.registration.github.clientId}") private val clientId: String,
                      @Value("\${spring.security.oauth2.client.registration.github.clientSecret}") private val clientSecret: String,
                      @Qualifier("github-web-client") private val restClient: RestClient) {
    private final val logger: Logger = LoggerFactory.getLogger(GithubAppClient::class.java)
    private final val basicAuth: String = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())

    companion object {
        const val ACCEPT_HEADER = "application/vnd.github+json"
        const val GITHUB_TOKEN_API_ENDPOINT = "/applications/{clientId}/token"
    }

    /**
     * Reset the GHO Token and return the new one.
     * @param token the GHO Token to be reset
     * @return the new GHO Token
     */
    fun resetOauthToken(token: String): String {
        val newToken = try {
            val resetTokenResponse = addHeaders(this.restClient.patch()
                    .uri(GITHUB_TOKEN_API_ENDPOINT, clientId)
                    .body(ResetTokenRequest(token)))
                    .retrieve()
                    .body(ResetTokenResponse::class.java)
            resetTokenResponse?.token
        } catch (e: Exception) {
            logger.error("Exception caught while resetting the GHO token", e);
            throw ResetTokenException("Exception thrown while resetting user GHO token. Token might be invalid!", e)
        }
        if (newToken == null) {
            throw ResetTokenException("GHO Token reset failed. Token might be invalid!")
        }
        return newToken
    }

    fun revokeOauthToken(token: String) {
        try {
            addHeaders(this.restClient.method(HttpMethod.DELETE)
                    .uri(GITHUB_TOKEN_API_ENDPOINT, clientId)
                    .body(RevokeTokenRequest(token)))
                    .retrieve()
        } catch (e: Exception) {
            // TODO: Change this exception
            logger.error("Exception caught while revoking the GHO token", e);
            throw ResetTokenException("Exception thrown while revoking user GHO token. Token might be invalid!", e)
        }
    }

    /**
     * Check if the GHO Token is valid.
     * @param token the GHO Token to be checked
     * @return true if the GHO Token is valid, false otherwise
     */
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
