package com.eddranca.containerws.service

import com.eddranca.containerws.persistence.InMemoryWorkspaceRepository
import com.eddranca.containerws.persistence.model.Workspace
import com.eddranca.containerws.persistence.model.WorkspaceStatus
import com.eddranca.containerws.service.exceptions.InvalidUserException
import com.eddranca.containerws.util.git.GitUtil
import com.eddranca.model.CreateWorkspaceRequest
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class WorkspaceService(private val workspaceRepository: InMemoryWorkspaceRepository,
                       private val gitUtil: GitUtil,
                       private val userService: UserService) {
    fun createWorkspace(userId: String, createWorkspaceRequest: CreateWorkspaceRequest) {
        val ghoToken = userService.getToken(userId) ?: throw InvalidUserException("User not found.", userId)
        CompletableFuture
                .supplyAsync {
                    createWorkspaceWithToken(ghoToken, createWorkspaceRequest)
                }
    }

    private fun createWorkspaceWithToken(ghoToken: String, createWorkspaceRequest: CreateWorkspaceRequest) {
        /*TODO("""go through the workspace creation workflow:
            1. add workspace to database with in progress status;
            2. clone repos
            3. create container/pod with repos copied over
            4. add workspace to database with completed status
            """)*/
        var directory: File? = null
        try {
            val workspace = Workspace(UUID.randomUUID(), createWorkspaceRequest.cpuLimit, createWorkspaceRequest.memoryLimit, WorkspaceStatus.IN_PROGRESS)
            workspaceRepository.createWorkspace(workspace)
            val cloneReposResponse = gitUtil.cloneGitHubRepos(createWorkspaceRequest.repositories, ghoToken)
            directory = cloneReposResponse.directory
            workspace.status = WorkspaceStatus.CREATED
            workspaceRepository.updateWorkspace(workspace)
        } catch (e: Exception) {

            throw e
        }
        finally {
            if (directory != null) {
                cleanDirectory(directory)
            }
        }
    }

    private fun cleanDirectory(directory: File) {
        val fileDeleted = directory.deleteRecursively()
        if (!fileDeleted) {
            //TODO: Store this somewhere and have a cron job for cleaning up. Can even be stored in memory
            // https://www.baeldung.com/spring-scheduled-tasks
        }
    }
}