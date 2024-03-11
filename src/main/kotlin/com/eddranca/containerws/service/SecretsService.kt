package com.eddranca.containerws.service

import org.springframework.stereotype.Service

@Service
class SecretsService {
    private val userGhoTokens = mutableMapOf<String, String>()
    fun getUserGHO(userId: String): String? {
        return userGhoTokens[userId]
    }

    fun storeUserGHO(userId: String, gho: String) {
        userGhoTokens[userId] = gho
    }

    fun removeUserGHO(userId: String) {
        userGhoTokens.remove(userId)
    }
}