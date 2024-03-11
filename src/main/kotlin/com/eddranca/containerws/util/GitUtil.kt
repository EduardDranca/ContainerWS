package com.eddranca.containerws.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.net.URI
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

    /**
     * Clones GitHub repositories based on the provided configuration map.
     *
     * @param repoUrls List containing GitHub repository URLs.
     * @throws InvalidGitHubRepoException If one or more GitHub repository URLs are invalid.
     */
    fun cloneGitHubRepos(repoUrls: List<URI>, ghoToken: String): List<File> {
        val invalidRepos = mutableListOf<URI>()

        repoUrls.forEach { repoUrl ->
            if (!isValidGitHubRepoUrl(repoUrl)) {
                invalidRepos.add(repoUrl)
            }
        }

        if (invalidRepos.isNotEmpty()) {
            logger.warn("The repoUrls list contained some invalid GitHub repo links: $invalidRepos.")
            throw InvalidGitHubRepoException("Invalid GitHub repository URLs: $invalidRepos", invalidRepos)
        }

        return repoUrls.map { repoUrl ->
            cloneRepo(repoUrl, ghoToken)
        }.toList()
    }


    /**
     * Validates if the given URL is a valid GitHub repository URL.
     *
     * @param url GitHub repository URL to be validated.
     * @return True if the URL is valid, false otherwise.
     */
    private fun isValidGitHubRepoUrl(url: URI): Boolean {
        val matcher = GITHUB_REPO_PATTERN.matcher(url.toString())
        return matcher.matches()
    }

    /**
     * Clones a GitHub repository using the "git clone" command.
     *
     * @param repoUrl GitHub repository URL to be cloned.
     */
    private fun cloneRepo(repoUrl: URI, ghoToken: String): File {
        val cloneDirectory = File("/Users/eduarddranca/repos")
        try {
            val processBuilder = ProcessBuilder("git", "clone", repoUrl.toString())
            processBuilder.directory(cloneDirectory)
            val process = processBuilder.start()
            // Write the username to the process input stream
            process.outputStream.write(("oauth2\n".encodeToByteArray()))
            process.outputStream.flush()

            // Write the password to the process input stream
            process.outputStream.write((ghoToken + "\n").encodeToByteArray())
            process.outputStream.flush()

            val exitCode = process.waitFor()
            if (exitCode == 0) {
                logger.info("Successfully cloned repository: $repoUrl")
            } else {
                logger.warn("Failed to clone repository: $repoUrl")
            }
        } catch (e: IOException) {
            logger.error("Error cloning repository: $repoUrl", e)
        } catch (e: InterruptedException) {
            logger.error("Interrupted while cloning repository: $repoUrl", e)
            Thread.currentThread().interrupt()
        }
        return File(cloneDirectory, repoUrl.path.substringAfterLast('/').dropLast(4))
    }
}

/**
 * Custom exception class to represent an exception related to invalid GitHub repositories.
 *
 * @param message The error message.
 * @param invalidRepos List of invalid GitHub repository URLs.
 */
class InvalidGitHubRepoException(override val message: String, val invalidRepos: List<URI>) : RuntimeException(message)
