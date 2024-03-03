package com.eddranca.containerws.controller

import com.eddranca.api.WorkspacesApi
import com.eddranca.containerws.service.WorkspaceService
import com.eddranca.model.CreateWorkspaceRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class WorkspaceController(private val workspaceService: WorkspaceService): WorkspacesApi {

    companion object {
        const val USER_ID_ATTRIBUTE = "id"
    }

    @PostMapping("/workspaces")
    override fun createWorkspace(createWorkspaceRequest: CreateWorkspaceRequest): ResponseEntity<Void> {
        val userId = extractUserId(SecurityContextHolder.getContext().authentication)
        workspaceService.createWorkspace(userId, createWorkspaceRequest)
        return ResponseEntity.accepted().build()
    }

    private fun extractUserId(authentication: Authentication): String {
        val oauth2User = authentication.principal as OAuth2User
        val userId = oauth2User.attributes[USER_ID_ATTRIBUTE] as String
        return userId
    }

}
