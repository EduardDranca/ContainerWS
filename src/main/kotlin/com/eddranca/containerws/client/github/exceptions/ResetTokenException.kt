package com.eddranca.containerws.client.github.exceptions

class ResetTokenException(override val message: String, override val cause: Throwable?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}
