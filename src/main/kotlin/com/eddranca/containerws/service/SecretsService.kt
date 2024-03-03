package com.eddranca.containerws.service

import org.springframework.stereotype.Service

@Service
class SecretsService {
    private val userGhoTokens = mutableMapOf<String, String>()
    fun getUserGHO(userId: String): String {
        return userGhoTokens[userId] ?: throw RuntimeException("User not found")
    }

    fun storeUserGHO(userId: String, gho: String) {
        userGhoTokens[userId] = gho
    }
}