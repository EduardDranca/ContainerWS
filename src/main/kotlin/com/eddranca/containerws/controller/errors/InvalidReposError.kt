package com.eddranca.containerws.controller.errors

data class InvalidReposError(val message: String, val repoUrls: List<String>) {
}