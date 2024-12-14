package com.eddranca.containerws.service.secrets

import org.springframework.stereotype.Component

@Component
class GhoTokenInMemoryStorageService: SecretsService<String, String> {
    private val userGhoTokens = mutableMapOf<String, String>()

    override fun getSecret(key: String): String? {
        return userGhoTokens[key]
    }

    override fun removeSecret(key: String) {
        userGhoTokens.remove(key)
    }

    override fun setSecret(key: String, value: String) {
        userGhoTokens[key] = value
    }
}