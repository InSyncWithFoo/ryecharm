package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


/**
 * Whether the exit code is 0.
 * 
 * Timeouts and cancellations must be handled separately.
 */
internal val ProcessOutput.isSuccessful: Boolean
    get() = exitCode == 0


internal val ProcessOutput.completedAbnormally: Boolean
    get() = isTimeout || isCancelled || !isSuccessful


@Suppress("unused")
@Serializable
internal class ProcessOutputSurrogate(
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val isTimeout: Boolean,
    val isCancelled: Boolean
) {
    override fun toString() = Json.encodeToString(this)
}


internal fun ProcessOutputSurrogate(processOutput: ProcessOutput) = with(processOutput) {
    ProcessOutputSurrogate(stdout, stderr, exitCode, isTimeout, isCancelled)
}
