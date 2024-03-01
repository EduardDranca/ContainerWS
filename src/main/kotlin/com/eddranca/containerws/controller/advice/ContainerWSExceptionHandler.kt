package com.eddranca.containerws.controller.advice

import com.eddranca.containerws.controller.errors.InvalidReposError
import com.eddranca.containerws.util.InvalidGitHubRepoException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

class ContainerWSExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(InvalidGitHubRepoException::class)
    fun handleInvalidGitHubRepoException(ex: InvalidGitHubRepoException): ResponseEntity<InvalidReposError> {
        return ResponseEntity.badRequest().body(InvalidReposError(ex.message, ex.invalidRepos))
    }
}