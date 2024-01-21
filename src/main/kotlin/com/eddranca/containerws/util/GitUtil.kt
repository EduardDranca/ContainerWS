package com.eddranca.containerws.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.DispatcherServlet
import org.springframework.web.servlet.FrameworkServlet
import java.io.IOException
import java.util.regex.Pattern


/**
 * Utility class for cloning GitHub repositories and validating their URLs.
 */
@Component
class GitUtil {

    companion object {
        private val GITHUB_REPO_PATTERN: Pattern = "^https://github\\.com/([\\w-]+)/([\\w-]+)\\.git$".toPattern()
    }
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private var notDone = true

    /**
     * Clones GitHub repositories based on the provided configuration map.
     *
     * @param repoUrls List containing GitHub repository URLs.
     * @throws InvalidGitHubRepoException If one or more GitHub repository URLs are invalid.
     */
    fun cloneGitHubRepos(repoUrls: List<String>, ghoToken: String) {
        val invalidRepos = mutableListOf<String>()

        repoUrls.forEach { repoUrl ->
            if (isValidGitHubRepoUrl(repoUrl)) {
                cloneRepo(repoUrl, ghoToken)
            } else {
                invalidRepos.add(repoUrl)
            }
        }

        if (invalidRepos.isNotEmpty()) {
            logger.warn("The repoUrls list contained some invalid GitHub repo links: $invalidRepos.")
            throw InvalidGitHubRepoException("Invalid GitHub repository URLs: $invalidRepos", invalidRepos)
        }
    }


    /**
     * Validates if the given URL is a valid GitHub repository URL.
     *
     * @param url GitHub repository URL to be validated.
     * @return True if the URL is valid, false otherwise.
     */
    private fun isValidGitHubRepoUrl(url: String): Boolean {
        val matcher = GITHUB_REPO_PATTERN.matcher(url)
        return matcher.matches()
    }

    /**
     * Clones a GitHub repository using the "git clone" command.
     *
     * @param repoUrl GitHub repository URL to be cloned.
     */
    private fun cloneRepo(repoUrl: String, ghoToken: String) {
        try {

            val processBuilder = ProcessBuilder("git", "clone", repoUrl)
            val process = processBuilder.start()
            if (notDone) {
                // Write the username to the process input stream
                process.outputStream.write(("oauth2\n".encodeToByteArray()))
                process.outputStream.flush()

                // Write the password to the process input stream
                process.outputStream.write((ghoToken + "\n").encodeToByteArray())
                process.outputStream.flush()
                notDone = true
            }

            val exitCode = process.waitFor()

            if (exitCode == 0) {
                logger.info("Successfully cloned repository: $repoUrl")
            } else {
                logger.warn("Failed to clone repository: $repoUrl")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Thread.currentThread().interrupt()
        }
    }
}

/**
 * Custom exception class to represent an exception related to invalid GitHub repositories.
 *
 * @param message The error message.
 * @param invalidRepos List of invalid GitHub repository URLs.
 */
class InvalidGitHubRepoException(message: String, val invalidRepos: List<String>) : RuntimeException(message)
