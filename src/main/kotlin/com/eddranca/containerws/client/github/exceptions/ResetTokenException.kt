package com.eddranca.containerws.client.github.exceptions

class ResetTokenException(override val message: String, override val cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
}
