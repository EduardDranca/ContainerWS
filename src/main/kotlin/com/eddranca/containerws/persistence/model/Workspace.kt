package com.eddranca.containerws.persistence.model

import java.util.*

data class Workspace(val id: UUID, val cpuLimit: Int, val memoryLimit: Int, var status: WorkspaceStatus)
