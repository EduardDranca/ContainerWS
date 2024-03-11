package com.eddranca.containerws.controller

import com.eddranca.api.WorkspacesApi
import com.eddranca.containerws.service.WorkspaceService
import com.eddranca.containerws.util.UserExtractorUtil
import com.eddranca.model.CreateWorkspaceRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class WorkspaceController(private val workspaceService: WorkspaceService,
                          private val userExtractorUtil: UserExtractorUtil): WorkspacesApi {

    @PostMapping("/workspaces")
    override fun createWorkspace(createWorkspaceRequest: CreateWorkspaceRequest): ResponseEntity<Void> {
        val userId = userExtractorUtil.extractUserId(SecurityContextHolder.getContext().authentication)
        workspaceService.createWorkspace(userId, createWorkspaceRequest)
        return ResponseEntity.accepted().build()
    }
}
