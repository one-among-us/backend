@file:Suppress("NAME_SHADOWING")

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

val auth = UsernamePasswordCredentialsProvider(secrets.githubToken, "")

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
    for (edit in edits) File(dir, edit.filePath).writeText(edit.content)

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

/**
 * Commit directly to the repository
 *
 * @param editor String
 * @param editorEmail String
 * @param edit One edit
 * @param message Commit message
 * @return Commit URL
 */
fun commitDirectly(editor: str, edit: DataEdit, message: str? = null): str
{
    val editor = editor.replace(" ", "-").lowercase()

    val token = secrets.githubToken
    val repo = secrets.githubRepo

    val github = GitHubBuilder().withOAuthToken(token).build()
    val ghRepo = github.getRepository(repo)
    val commit = ghRepo.createContent().path(edit.filePath).content(edit.content)
        .message(message ?: "User $editor pushed an edit").commit()

    return commit.commit!!.url.toString()
}
