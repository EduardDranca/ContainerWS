package com.eddranca.containerws.service.secrets

interface SecretsService<K, V> {
    fun getSecret(key: K): V?
    fun setSecret(key: K, value: V)
    fun removeSecret(key: K)
}