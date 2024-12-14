package com.eddranca.containerws.controller.advice

import com.eddranca.containerws.service.exceptions.InvalidUserException
import com.eddranca.containerws.util.git.InvalidGitHubRepoException
import com.eddranca.model.InvalidRepositoriesError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ContainerWSExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(InvalidGitHubRepoException::class)
    fun handleInvalidGitHubRepoException(ex: InvalidGitHubRepoException): ResponseEntity<InvalidRepositoriesError> {
        return ResponseEntity.badRequest().body(InvalidRepositoriesError()
                .message(ex.message)
                .repositories(ex.invalidRepos))
    }

    @ExceptionHandler(InvalidUserException::class)
    fun handleInvalidUserException(ex: InvalidUserException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.message)
    }
}