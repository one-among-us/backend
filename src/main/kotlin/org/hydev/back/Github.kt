@file:Suppress("NAME_SHADOWING")

package org.hydev.back

import org.kohsuke.github.GitHubBuilder

data class DataEdit(
    val filePath: str,
    val content: str
)

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

    return commit.commit!!.htmlUrl.toString()
}

fun main() {
    commitDirectly("test", DataEdit("people/tdor/comments/${date("yyyy-MM-dd")}-C17001.json", "{}"), "[+] Comment added by test for test")
}