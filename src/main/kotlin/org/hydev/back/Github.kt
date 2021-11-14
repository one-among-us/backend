package org.hydev.back

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.kohsuke.github.GitHubBuilder
import java.io.File

data class DataEdit(
    val filePath: str,
    val content: str
)

data class Secrets(
    val githubToken: str,
    val githubRepo: str
)

fun getSecretFromEnv(): Secrets?
{
    if (System.getenv("github-token") == null) return null
    return Secrets(System.getenv("github-token"), System.getenv("github-repo"))
}

fun getSecretsFromFile(): Secrets?
{
    val file = File("./secrets/github.txt")
    if (!file.exists() || !file.isFile) return null
    val text = file.readText()
    val lines = text.replace("\r\n", "\n").split("\n")
    return Secrets(lines[0], lines[1])
}

fun getSecrets(): Secrets
{
    val secrets = getSecretsFromFile() ?: getSecretFromEnv()
    if (secrets == null)
    {
        val dir = File("./secrets/github.txt").absolutePath
        throw RuntimeException("No secrets defined in $dir or in environment variables")
    }
    return secrets
}

/**
 * Create pull request
 *
 * @param editor String
 * @param editorEmail String
 * @param edits ArrayList<DataEdit>
 * @return Pull request url
 */
fun createPullRequest(editor: str, editorEmail: str, edits: list<DataEdit>): str
{
    val editor = editor.replace(" ", "-").lowercase()

    val token = secrets.githubToken
    val repo = secrets.githubRepo
    val auth = UsernamePasswordCredentialsProvider(token, "")
    val date = date("yyyy-MM-dd-HH-mm-ss")
    val branch = "edit-by-${editor}-${date}"

    // Clone repo
    val dir = File("./temp/github-repo-${editor}-${date}")
    val git = Git.cloneRepository()
        .setURI("https://${token}@github.com/$repo")
        .setDirectory(dir)
        .setCredentialsProvider(auth)
        .call()

    // Create branch
    git.checkout().setCreateBranch(true).setName(branch).call()

    // Change files
    for (edit in edits)
    {
        val file = File(dir, edit.filePath)
        file.writeText(edit.content)
    }

    // Git commit
    git.add().addFilepattern(".").call()
    git.commit().setAuthor(editor, editorEmail).setCommitter(editor, editorEmail)
        .setMessage("User $editor suggested ${edits.size} edits")
        .call()

    // Git push
    git.push().setCredentialsProvider(auth)
        .setRemote("origin")
        .setRefSpecs(RefSpec("$branch:$branch")).call()

    // Github pull request
    val github = GitHubBuilder().withOAuthToken(token).build()
    val ghRepo = github.getRepository(repo)
    val result = ghRepo.createPullRequest("[PR] User $editor suggested ${edits.size} edits",
        "${repo.split("/")[0]}:$branch", "main", "", true)

    git.close()

    // Delete local folder
    dir.deleteRecursively()
    dir.delete()

    return "https://github.com/$repo/pull/${result.number}"
}
