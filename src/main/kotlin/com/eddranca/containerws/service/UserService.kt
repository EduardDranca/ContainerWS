package com.eddranca.containerws.service

import com.eddranca.containerws.client.github.GithubAppClient
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class UserService(val githubClient: GithubAppClient): DefaultOAuth2UserService() {
    private val userMap: MutableMap<Int, String> = HashMap()
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)
        userMap[oauth2User.attributes["id"]!! as Int] = userRequest.accessToken.tokenValue
        return oauth2User
    }

    fun getToken(clientId: Int): String? {
        return userMap[clientId]
    }

    fun resetToken(clientId: Int): String {
        userMap.computeIfPresent(clientId) { _, currentToken -> githubClient.resetOauthToken(currentToken) }
        return userMap[clientId] ?: throw RuntimeException("blabla")
    }
}
