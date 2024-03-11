package com.eddranca.containerws.service.exceptions

class InvalidUserException(override val message: String?, val userId: String): Exception(message)