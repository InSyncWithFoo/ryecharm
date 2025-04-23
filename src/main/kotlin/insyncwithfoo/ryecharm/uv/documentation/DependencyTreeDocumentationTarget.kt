package insyncwithfoo.ryecharm.uv.documentation

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.platform.backend.documentation.DocumentationResult
import com.intellij.psi.PsiElement
import insyncwithfoo.ryecharm.ElementBasedDocumentationTarget
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.PackageName
import insyncwithfoo.ryecharm.configurations.uv.uvConfigurations
import insyncwithfoo.ryecharm.definition
import insyncwithfoo.ryecharm.interpreterPath
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.popup
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.runUnderIOThread
import insyncwithfoo.ryecharm.toDocumentationResult
import insyncwithfoo.ryecharm.toHTML
import insyncwithfoo.ryecharm.uv.commands.uv
import insyncwithfoo.ryecharm.wrappedInCodeBlock


internal class DependencyTreeDocumentationTarget(
    override val element: PsiElement,
    private val `package`: PackageName,
    private val inverted: Boolean,
) : ElementBasedDocumentationTarget() {
    
    override fun fromDereferenced(element: PsiElement) =
        DependencyTreeDocumentationTarget(element, `package`, inverted)
    
    override fun computeDocumentation() = DocumentationResult.asyncDocumentation {
        project.getDocumentation()?.toDocumentationResult()
    }
    
    private suspend fun Project.getDocumentation(): HTML? {
        val uv = this.uv ?: return null
        val configurations = uvConfigurations
        
        // TODO: Use `uv tree` instead?
        val command = uv.pipTree(
            `package`,
            inverted,
            showVersionSpecifiers = configurations.showVersionSpecifiersForDependencies,
            showLatestVersions = configurations.showLatestVersionsForDependencies,
            dedupe = configurations.dedupeDependencyTrees,
            depth = configurations.dependencyTreeDepth,
            interpreter = interpreterPath
        )
        
        val output = runUnderIOThread {
            runInBackground(command)
        }
        
        if (output.isTimeout) {
            processTimeout(command)
            return null
        }
        
        if (output.isCancelled) {
            return null
        }
        
        return readAction {
            makePopup(output).toString()
        }
    }
    
    private fun makePopup(output: ProcessOutput) =
        when (output.isSuccessful) {
            true -> when (output.stdout.isNotBlank()) {
                true -> makeTreePopup(output.stdout)
                else -> makeEmptyPopup()
            }
            else -> makeErrorPopup(output.stderr)
        }
    
    private fun makeEmptyPopup() = popup {
        val content = message("documentation.popup.dependencytree.empty", `package`)
        
        content(content.toHTML())
    }
    
    private fun makeTreePopup(stdout: String) = popup {
        val lead = when (inverted) {
            true -> message("documentation.popup.dependencytree.tree.lead.inverted", `package`)
            else -> message("documentation.popup.dependencytree.tree.lead", `package`)
        }
        val content = """
            |$lead
            |
            |${stdout.wrappedInCodeBlock("text")}
        """.trimMargin()
        
        definition(`package`, "requirements")
        
        separator()
        
        content(content.toHTML())
    }
    
    private fun makeErrorPopup(stderr: String) = popup {
        val lead = message("documentation.popup.dependencytree.error", `package`)
        val content = """
            |$lead
            |
            |${stderr.wrappedInCodeBlock("text")}
        """.trimMargin()
        
        content(content.toHTML())
    }
    
}
