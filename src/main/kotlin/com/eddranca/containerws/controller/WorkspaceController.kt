package com.eddranca.containerws.controller

import com.eddranca.api.WorkspacesApi
import com.eddranca.containerws.service.UserService
import com.eddranca.containerws.util.GitUtil
import com.eddranca.model.CreateWorkspaceRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
class WorkspaceController(val gitUtil: GitUtil, val userService: UserService): WorkspacesApi {

    @PostMapping("/workspaces")
    override fun createWorkspace(@Valid @RequestBody createWorkspaceRequest: CreateWorkspaceRequest): ResponseEntity<Void> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication.principal !is OAuth2User) {
            throw RuntimeException("exception")
        }
        val oauth2User = authentication.principal as OAuth2User
        userService.getToken(oauth2User.attributes["id"]!! as Int)?.let { gitUtil.cloneGitHubRepos(createWorkspaceRequest.repositories, it) }
        return ResponseEntity.accepted().build()
    }

}
