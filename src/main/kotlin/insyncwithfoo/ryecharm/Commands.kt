package insyncwithfoo.ryecharm

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.util.io.toByteArray
import insyncwithfoo.ryecharm.configurations.HasTimeouts
import insyncwithfoo.ryecharm.configurations.PanelBasedConfigurable
import kotlinx.coroutines.CoroutineScope
import java.nio.CharBuffer
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension


internal typealias MillisecondsOrNoLimit = Int
internal typealias Arguments = List<String>


/**
 * @see com.intellij.credentialStore.toByteArrayAndClear
 */
private fun CharArray.toByteArrayAndClear(): ByteArray {
    val charBuffer = CharBuffer.wrap(this)
    val byteBuffer = Charsets.UTF_8.encode(charBuffer)
    
    fill(0.toChar())
    
    return byteBuffer.toByteArray(isClear = true)
}


internal interface CommandWithTimeout {
    
    val timeoutKey: String
    val configurable: Class<out PanelBasedConfigurable<*>>
    
    fun getTimeout(project: Project?): MillisecondsOrNoLimit?
    
}


internal abstract class Command {
    
    abstract val subcommand: String?
    
    lateinit var executable: Path
    lateinit var arguments: List<String>
    
    var stdin: String? = null
    
    var workingDirectory: Path? = null
    
    open val runningMessage: String
        get() = message("progresses.command.default")
    
    private val fragments: List<String>
        get() = listOfNotNull(
            executable.toString(),
            subcommand,
            *arguments.toTypedArray()
        )
    
    private val commandLine: GeneralCommandLine
        get() = GeneralCommandLine(fragments).apply {
            withWorkingDirectory(this@Command.workingDirectory)
            withCharset(Charsets.UTF_8)
        }
    
    val processHandler: CapturingProcessHandler
        get() = CapturingProcessHandler(commandLine).apply {
            if (stdin != null) {
                processInput.write(stdin!!.toCharArray().toByteArrayAndClear())
                processInput.close()
            }
        }
    
    /**
     * A simple form to be used in notifications and such.
     */
    val shortenedForm: String
        get() = "${executable.nameWithoutExtension} ${subcommand.orEmpty()}".trimEnd()
    
    override fun toString() = commandLine.commandLineString
    
    fun run(timeout: MillisecondsOrNoLimit): ProcessOutput {
        LOGGER.info("Running: $this")
        
        return processHandler.runProcess(timeout).also {
            LOGGER.info("Output: ${ProcessOutputSurrogate(it)}")
        }
    }
    
    companion object {
        @JvmStatic
        protected val LOGGER = Logger.getInstance(Command::class.java)
    }
    
}


internal abstract class CommandFactory {
    
    abstract val executable: Path
    abstract val workingDirectory: Path?
    
    protected fun Command.setExecutableAndWorkingDirectory() = this.apply {
        executable = this@CommandFactory.executable
        workingDirectory = this@CommandFactory.workingDirectory
    }
    
}


private typealias CommandRunner = suspend CoroutineScope.(Command) -> ProcessOutput
private typealias UICallback = suspend CoroutineScope.(ProcessOutput) -> Unit


private fun Project.getCommandTimeout(command: Command) =
    (command as? CommandWithTimeout)?.getTimeout(this) ?: HasTimeouts.NO_LIMIT


internal suspend fun Project.runInBackground(command: Command) =
    runInBackground(command.runningMessage) {
        command.run(getCommandTimeout(command))
    }


internal suspend fun Project.runInForeground(command: Command) =
    runInForeground(command.runningMessage) {
        command.run(getCommandTimeout(command))
    }


private suspend inline fun runIOCommandThenUICallback(
    command: Command,
    crossinline runCommand: CommandRunner,
    crossinline callback: UICallback
) {
    val output = ProgressContext.IO.compute {
        runCommand(command)
    }
    
    ProgressContext.UI.launch {
        callback(output)
    }
}


internal suspend fun Project.runInBackground(command: Command, callback: UICallback) =
    runIOCommandThenUICallback(command, { runInBackground(it) }, callback)


internal suspend fun Project.runInForeground(command: Command, callback: UICallback) =
    runIOCommandThenUICallback(command, { runInForeground(it) }, callback)
