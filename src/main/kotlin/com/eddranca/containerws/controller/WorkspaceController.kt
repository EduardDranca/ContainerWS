package com.eddranca.containerws.controller

import com.eddranca.containerws.service.UserService
import com.eddranca.containerws.util.GitUtil
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.ForkJoinPool
import java.util.stream.IntStream


@RestController
class WorkspaceController(val gitUtil: GitUtil, val userService: UserService) {
    @GetMapping("/workspaces")
    fun createWorkspace(@RequestParam repo: String): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val delegator = DelegatingSecurityContextExecutorService(ForkJoinPool())
        val completionService = ExecutorCompletionService<Boolean>(delegator)
        val fs = IntStream.range(0, 100)
            .mapToObj {
                completionService.submit(Callable { it % 2 == 0 })
            }

        if (authentication.principal !is OAuth2User) {
            throw RuntimeException("exception")
        }
        val oauth2User = authentication.principal as OAuth2User
        userService.getToken(oauth2User.attributes["id"]!! as Int)?.let { gitUtil.cloneGitHubRepos(listOf(repo), it) }
        return ""
    }

}
