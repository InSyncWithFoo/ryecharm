package insyncwithfoo.ryecharm

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import insyncwithfoo.ryecharm.icons.RyeIcons


internal fun Project?.showMessage(title: String, message: String) {
    Messages.showMessageDialog(this, message, title, RyeIcons.SMALL_32)
}


internal fun Project?.showMessage(message: String) {
    showMessage(message("messages.processResult.title"), message)
}


internal fun somethingIsWrong(title: String, message: String, project: Project? = null) {
    Messages.showErrorDialog(project, message, title)
}

internal fun somethingIsWrong(message: String, project: Project? = null) {
    somethingIsWrong(title = message("messages.somethingIsWrong.title"), message, project)
}

internal fun Project?.somethingIsWrong(title: String, message: String) {
    somethingIsWrong(title, message, project = this)
}

internal fun Project?.somethingIsWrong(message: String) {
    somethingIsWrong(message, project = this)
}
