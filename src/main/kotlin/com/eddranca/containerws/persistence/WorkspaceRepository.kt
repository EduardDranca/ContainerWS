package com.eddranca.containerws.persistence

import com.eddranca.containerws.persistence.model.Workspace
import java.util.*

interface WorkspaceRepository {
    fun createWorkspace(workspace: Workspace): Workspace
    fun updateWorkspace(workspace: Workspace): Workspace
    fun deleteWorkspace(id: UUID)
}