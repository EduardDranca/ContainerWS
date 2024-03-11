package com.eddranca.containerws.util

import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component

@Component
class UserExtractorUtil {
    companion object {
        private const val USER_ID_ATTRIBUTE = "id"
    }

    fun extractUserId(authentication: Authentication): String {
        val oauth2User = authentication.principal as OAuth2User
        return extractUserId(oauth2User)
    }

    fun extractUserId(oAuth2User: OAuth2User): String {
        return oAuth2User.attributes[USER_ID_ATTRIBUTE].toString()
    }
}