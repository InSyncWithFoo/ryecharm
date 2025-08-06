package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.parseAsJSON
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.unknownError


internal fun Project.runCheckCommand(command: Command): List<Diagnostic>? {
    val output = runBlockingCancellable {
        runInBackground(command)
    }
    val results = output.stdout.parseAsJSON<List<Diagnostic>>()
    
    if (output.isCancelled) {
        return null
    }
    
    if (output.isTimeout) {
        processTimeout(command)
        return null
    }
    
    if (!output.isSuccessful || results == null) {
        unknownError(command, output)
        return null
    }
    
    return results
}
