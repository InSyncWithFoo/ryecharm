package insyncwithfoo.ryecharm.ruff.documentation

import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.HTML
import insyncwithfoo.ryecharm.Markdown
import insyncwithfoo.ryecharm.ProgressContext
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.toHTML


private suspend fun Project.getMarkdownDocumentationForRule(rule: String): Markdown? {
    val ruff = this.ruff ?: return null
    val command = ruff.rule(rule)
    
    val output = ProgressContext.IO.compute {
        runInBackground(command)
    }
    
    if (output.isTimeout) {
        processTimeout(command)
        return null
    }
    
    if (output.isCancelled || !output.isSuccessful) {
        return null
    }
    
    return output.stdout
}


internal suspend fun Project.getDocumentationForRule(rule: String): HTML? {
    val markdownDocumentation = getMarkdownDocumentationForRule(rule) ?: return null
    
    return readAction { markdownDocumentation.toHTML() }
}
