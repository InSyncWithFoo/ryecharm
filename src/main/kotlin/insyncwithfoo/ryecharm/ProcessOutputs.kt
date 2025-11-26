package insyncwithfoo.ryecharm

import com.intellij.execution.process.ProcessOutput
import kotlinx.serialization.Serializable


/**
 * Whether the exit code is 0.
 * 
 * Timeouts and cancellations must be handled separately.
 */
internal val ProcessOutput.isSuccessful: Boolean
    get() = exitCode == 0


/**
 * Whether the process is cancelled, timed out or unsuccessful.
 * 
 * @see [isSuccessful]
 */
internal val ProcessOutput.completedAbnormally: Boolean
    get() = isTimeout || isCancelled || !isSuccessful


/**
 * [ProcessOutput] in serializable form,
 * to be used in logging functions.
 */
@Serializable
@Suppress("unused")
internal class ProcessOutputSurrogate(
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val isTimeout: Boolean,
    val isCancelled: Boolean
) {
    override fun toString() = this.stringifyToPrettyJSON()
}


internal fun ProcessOutput.toSurrogate() =
    ProcessOutputSurrogate(stdout, stderr, exitCode, isTimeout, isCancelled)
