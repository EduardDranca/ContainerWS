package com.eddranca.containerws.service

import com.eddranca.containerws.client.github.GithubAppClient
import com.eddranca.containerws.client.github.exceptions.ResetTokenException
import com.eddranca.containerws.service.secrets.SecretsService
import com.eddranca.containerws.util.UserExtractorUtil
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class UserService(private val githubClient: GithubAppClient,
                  private val userExtractorUtil: UserExtractorUtil,
                  private val secretsService: SecretsService<String, String>): DefaultOAuth2UserService() {
    /**
     * @param userRequest The OAuth2UserRequest object containing the user's access token.
     * @return The OAuth2User object containing the user's information.
     */
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)
        val userId = userExtractorUtil.extractUserId(oauth2User)
        val userToken = getToken(userId)
        if (userToken == null || !githubClient.isTokenValid(userToken)) {
            secretsService.setSecret(userId, userRequest.accessToken.tokenValue)
        }
        return oauth2User;
    }

    /**
     * Get the user's GitHub access token from the secret service.
     * @param userId The ID of the user.
     * @return The user's GitHub access token.
     */
    fun getToken(userId: String): String? {
        // TODO: Think about reauthenticating the user here if necessary
        return secretsService.getSecret(userId)
    }

    /**
     * Reset the user's GitHub access token and stores the new token in the secrets service.
     * If the reset fails and the old token is still valid, the old token is kept in the secrets service.
     * @param userId The ID of the user.
     * @return The new GitHub access token.
     */
    fun resetUserToken(userId: String): String {
        // TODO: Create a better exception
        val oldUserToken = secretsService.getSecret(userId) ?: throw RuntimeException("Token not found for user: $userId")
        try {
            val newUserToken = githubClient.resetOauthToken(oldUserToken)
            secretsService.setSecret(userId, newUserToken)
            return newUserToken
        } catch (e: ResetTokenException) {
            if (githubClient.isTokenValid(oldUserToken)) {
                return oldUserToken
            }
            secretsService.removeSecret(userId)
            throw e
        }
    }
}
