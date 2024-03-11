package com.eddranca.containerws.service

import com.eddranca.containerws.service.exceptions.InvalidUserException
import com.eddranca.containerws.util.GitUtil
import com.eddranca.model.CreateWorkspaceRequest
import org.springframework.stereotype.Service

@Service
class WorkspaceService(private val secretsService: SecretsService,
                       private val gitUtil: GitUtil) {
    fun createWorkspace(userId: String, createWorkspaceRequest: CreateWorkspaceRequest) {
        val ghoToken = secretsService.getUserGHO(userId) ?: throw InvalidUserException("User not found.", userId)
        gitUtil.cloneGitHubRepos(createWorkspaceRequest.repositories, ghoToken)
        return
    }
}