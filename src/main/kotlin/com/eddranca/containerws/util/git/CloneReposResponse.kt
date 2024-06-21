package com.eddranca.containerws.util.git

import java.io.File

data class CloneReposResponse(val directory: File, val repositories: List<File>)