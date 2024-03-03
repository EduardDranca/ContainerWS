package com.eddranca.containerws.service

import com.eddranca.model.CreateWorkspaceRequest
import org.springframework.stereotype.Service

@Service
class WorkspaceService(private val userService: UserService) {
    fun createWorkspace(userId: String, createWorkspaceRequest: CreateWorkspaceRequest) {
        return
    }
}