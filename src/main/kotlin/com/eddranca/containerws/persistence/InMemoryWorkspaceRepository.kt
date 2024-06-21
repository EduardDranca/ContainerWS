package com.eddranca.containerws.persistence

import com.eddranca.containerws.persistence.model.Workspace
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class InMemoryWorkspaceRepository : WorkspaceRepository {
    private val workspaces = HashMap<UUID, Workspace>()
    override fun createWorkspace(workspace: Workspace): Workspace {
        if (workspaces.containsKey(workspace.id)) {
            throw RuntimeException("Workspace already exists")
        }
        workspaces[workspace.id] = workspace
        return workspace
    }

    override fun updateWorkspace(workspace: Workspace): Workspace {
        if (!workspaces.containsKey(workspace.id)) {
            throw RuntimeException("Workspace not found")
        }
        workspaces[workspace.id] = workspace
        return workspace
    }

    override fun deleteWorkspace(id: UUID) {
        workspaces.remove(id)
    }
}