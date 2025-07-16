package insyncwithfoo.ryecharm.uv.intentions

import com.intellij.codeInsight.intention.impl.IntentionActionWithTextCaching
import com.intellij.codeInsight.intention.impl.IntentionContainer
import com.intellij.codeInsight.intention.impl.IntentionGroup
import com.intellij.codeInsight.intention.impl.IntentionHintComponent
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.scale.JBUIScale.scale
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.Dialog
import insyncwithfoo.ryecharm.ExternalIntentionAction
import insyncwithfoo.ryecharm.WriteIntentionAction
import insyncwithfoo.ryecharm.bindSelected
import insyncwithfoo.ryecharm.couldNotConstructCommandFactory
import insyncwithfoo.ryecharm.fileDocumentManager
import insyncwithfoo.ryecharm.fileEditorManager
import insyncwithfoo.ryecharm.isPyprojectToml
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.launch
import insyncwithfoo.ryecharm.message
import insyncwithfoo.ryecharm.noProjectFound
import insyncwithfoo.ryecharm.notifyErrorFromOutput
import insyncwithfoo.ryecharm.notifyIfProcessIsUnsuccessfulOr
import insyncwithfoo.ryecharm.notifyWarningsFromOutput
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.processCompletedSuccessfully
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.radioButtonFor
import insyncwithfoo.ryecharm.runInForeground
import insyncwithfoo.ryecharm.saveAllDocumentsAsIs
import insyncwithfoo.ryecharm.unknownError
import insyncwithfoo.ryecharm.uv.commands.UV
import insyncwithfoo.ryecharm.uv.commands.VersionBumpType
import insyncwithfoo.ryecharm.uv.commands.uv
import insyncwithfoo.ryecharm.uv.commands.version


private fun VirtualFile.refresh() {
    val (asynchronous, recursive) = Pair(false, false)
    
    refresh(asynchronous, recursive)
}


private fun FileEditorManager.refreshAllEditors() {
    allEditors.forEach { it.file.refresh() }
}


private fun Project.notifyNewVersion(command: Command, output: ProcessOutput) {
    val newVersion = output.stdout.trim().takeIf { it.isNotBlank() }
        ?: return unknownError(command, output)
    
    val successfulContent = message("notifications.versionSetTo.title", newVersion)
    processCompletedSuccessfully(successfulContent)
}


private fun Project.notifyErrorOrNewVersion(command: Command, output: ProcessOutput) {
    if (output.isCancelled) {
        return
    }
    
    if (output.isTimeout) {
        return processTimeout(command)
    }
    
    notifyWarningsFromOutput(output)
    
    if (!output.isSuccessful) {
        return notifyErrorFromOutput(output)
    }
    
    notifyNewVersion(command, output)
}


private fun Project.runCommandAndLoadOutput(command: Command, file: PsiFile?) = launch<IntentionCoroutine> {
    val output = runInForeground(command)
    
    when (file) {
        null -> fileEditorManager.refreshAllEditors()
        else -> file.virtualFile?.refresh()
    }
    
    notifyErrorOrNewVersion(command, output)
}


private fun Project.doBumping(bumpType: VersionBumpType, file: PsiFile?) {
    if (this.path == null) {
        return noProjectFound()
    }
    
    val uv = this.uv
    
    if (uv == null) {
        return couldNotConstructCommandFactory<UV>(
            """
            |Was trying to bump project version.
            """.trimMargin()
        )
    }
    
    val command = uv.version(bumpType)
    
    runCommandAndLoadOutput(command, file)
}


private class Bumper(private val bumpType: VersionBumpType) :
    ExternalIntentionAction, WriteIntentionAction, DumbAware
{
    
    override fun getFamilyName() =
        message("intentions.uv.bumpProjectVersion.familyName")
    
    override fun getText() = bumpType.toString()
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) =
        editor != null && file?.virtualFile?.isPyprojectToml == true
    
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val document = file?.viewProvider?.document ?: return
        
        fileDocumentManager.saveDocumentAsIs(document)
        project.doBumping(bumpType, file)
    }
    
}


private class BumperContainer() : IntentionContainer {
    
    override fun getTitle() = message("dialogs.bumpProjectVersion.title")
    
    override fun getAllActions() =
        VersionBumpType.entries.map {
            IntentionActionWithTextCaching(Bumper(it))
        }
    
    override fun getGroup(action: IntentionActionWithTextCaching) = IntentionGroup.OTHER
    
    override fun getIcon(action: IntentionActionWithTextCaching) = null
    
}


private class BumpingKindChoosingDialog(project: Project) : Dialog(project) {
    
    var bumpType = VersionBumpType.MAJOR
    
    init {
        init()
        
        @Suppress("DialogTitleCapitalization")
        title = message("dialogs.bumpProjectVersion.title")
        widthAndHeight = scale(300) to scale(400)
    }
    
    override fun createCenterPanel() = panel {
        row { label(message("dialogs.bumpProjectVersion.label")) }
        
        val group = buttonsGroup(indent = true) {
            row { radioButtonFor(VersionBumpType.MAJOR).focused() }
            row { radioButtonFor(VersionBumpType.MINOR) }
            row { radioButtonFor(VersionBumpType.PATCH) }
            row { radioButtonFor(VersionBumpType.STABLE) }
            row { radioButtonFor(VersionBumpType.ALPHA) }
            row { radioButtonFor(VersionBumpType.BETA) }
            row { radioButtonFor(VersionBumpType.RC) }
            row { radioButtonFor(VersionBumpType.POST) }
            row { radioButtonFor(VersionBumpType.DEV) }
        }
        group.bindSelected(::bumpType)
    }
    
}


internal class BumpProjectVersion : AnAction(), ExternalIntentionAction, WriteIntentionAction, DumbAware {
    
    override fun getFamilyName() =
        message("intentions.uv.bumpProjectVersion.familyName")
    
    override fun getText() = familyName
    
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?) =
        editor != null && file?.virtualFile?.isPyprojectToml == true
    
    /**
     * @see com.intellij.lang.impl.modcommand.ModCommandExecutorImpl.executeChoose
     */
    @Suppress("UnstableApiUsage")
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor != null && file != null) {
            val container = BumperContainer()
            val showExpanded = true
            
            IntentionHintComponent.showIntentionHint(project, file, editor, showExpanded, container)
        }
    }
    
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return noProjectFound()
        val dialog = BumpingKindChoosingDialog(project)
        
        if (dialog.showAndGet()) {
            fileDocumentManager.saveAllDocumentsAsIs()
            project.doBumping(dialog.bumpType, file = null)
        }
    }
    
}
