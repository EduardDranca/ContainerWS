package com.eddranca.containerws.service

import com.eddranca.containerws.client.github.GithubAppClient
import com.eddranca.containerws.client.github.model.ResetTokenRequest
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class UserService(private val githubClient: GithubAppClient, private val secretsService: SecretsService): DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)
        val userId = oauth2User.attributes["id"] as String
        secretsService.storeUserGHO(userId, userRequest.accessToken.tokenValue)
        return oauth2User
    }

    fun getToken(userId: String): String? {
        return secretsService.getUserGHO(userId)
    }

    fun resetToken(userId: String): String {
        // TODO: If token reset fails, we could check if the old token is still valid.
        // If the token is still valid, we can still use it, if not,
        // then we'll have to prompt the user to reload the website in their browser to hopefully get a new token?
        val oldUserToken = secretsService.getUserGHO(userId)
        val newUserToken = githubClient.resetOauthToken(ResetTokenRequest(oldUserToken))
        secretsService.storeUserGHO(userId, newUserToken)
        return newUserToken
    }
}
