package insyncwithfoo.ryecharm.ruff.linting

import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.deserializationError
import insyncwithfoo.ryecharm.isSuccessful
import insyncwithfoo.ryecharm.parseAsJSONStrictly
import insyncwithfoo.ryecharm.processTimeout
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.unknownError
import kotlinx.serialization.SerializationException


internal fun Project.runCheckCommand(command: Command): List<Diagnostic>? {
    val output = runBlockingCancellable {
        runInBackground(command)
    }
    
    if (output.isCancelled) {
        return null
    }
    
    if (output.isTimeout) {
        processTimeout(command)
        return null
    }
    
    val results = try {
        output.stdout.parseAsJSONStrictly<List<Diagnostic>>()
    } catch (error: SerializationException) {
        deserializationError(command, output, error)
        return null
    }
    
    if (!output.isSuccessful) {
        unknownError(command, output)
        return null
    }
    
    return results
}
