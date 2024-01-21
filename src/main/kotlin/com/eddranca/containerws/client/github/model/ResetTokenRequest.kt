package com.eddranca.containerws.client.github.model

import com.fasterxml.jackson.annotation.JsonProperty

class ResetTokenRequest(@JsonProperty("auth_token") val authToken: String)
