package com.eddranca.containerws.client.github.model

import com.fasterxml.jackson.annotation.JsonProperty

class RevokeTokenRequest(@JsonProperty("access_token") val accessToken: String)