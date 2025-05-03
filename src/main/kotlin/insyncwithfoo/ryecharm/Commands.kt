package insyncwithfoo.ryecharm

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.project.Project
import com.intellij.util.io.toByteArray
import insyncwithfoo.ryecharm.common.logging.redKnotLogger
import insyncwithfoo.ryecharm.common.logging.ruffLogger
import insyncwithfoo.ryecharm.common.logging.ryeLogger
import insyncwithfoo.ryecharm.common.logging.uvLogger
import insyncwithfoo.ryecharm.ty.commands.TyCommand
import insyncwithfoo.ryecharm.ruff.commands.RuffCommand
import insyncwithfoo.ryecharm.rye.commands.RyeCommand
import insyncwithfoo.ryecharm.uv.commands.UVCommand
import java.nio.CharBuffer
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension


/**
 * Provide ergonomic means to construct [Command]s.
 * 
 * Each and every public method of a factory
 * must infallibly return a [Command].
 */
internal abstract class CommandFactory {
    
    abstract val executable: Path
    abstract val workingDirectory: Path?
    
    abstract fun CommandArguments.withGlobalOptions(): CommandArguments
    
    private fun Command.setExecutableAndWorkingDirectory() = this.apply {
        executable = this@CommandFactory.executable
        workingDirectory = this@CommandFactory.workingDirectory
    }
    
    protected open fun Command.build(arguments: CommandArguments? = null, stdin: String? = null) = this.apply {
        this.arguments = arguments?.withGlobalOptions()?.toList() ?: emptyList()
        this.stdin = stdin
        
        setExecutableAndWorkingDirectory()
    }
    
}


/**
 * @see com.intellij.credentialStore.toByteArrayAndClear
 */
private fun CharArray.toByteArrayAndClear(): ByteArray {
    val charBuffer = CharBuffer.wrap(this)
    val byteBuffer = Charsets.UTF_8.encode(charBuffer)
    
    fill(0.toChar())
    
    return byteBuffer.toByteArray(isClear = true)
}


/**
 * Represent a generic command that can be run
 * and displayed in a user-friendly manner.
 * 
 * @see Command.run
 * @see Command.runningMessage
 * @see Command.shortenedForm
 */
internal abstract class Command {
    
    /**
     * @see shortenedForm
     */
    abstract val subcommands: List<String>
    
    lateinit var executable: Path
    lateinit var arguments: List<String>
    
    var stdin: String? = null
    
    var workingDirectory: Path? = null
    
    /**
     * A message to be displayed in the progress bar
     * while the command is running.
     */
    open val runningMessage: String
        get() = message("progresses.command.default")
    
    private val fragments: List<String>
        get() = listOfNotNull(
            executable.toString(),
            *subcommands.toTypedArray(),
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
    
    private val executableName: String
        get() = executable.nameWithoutExtension
    
    /**
     * A simple form to be used in notifications and such.
     */
    val shortenedForm: String
        get() = when (subcommands.isEmpty()) {
            true -> executableName
            else -> "$executableName ${subcommands.joinToString(" ")}"
        }
    
    override fun toString() = commandLine.commandLineString
    
    /**
     * Run the command using [CapturingProcessHandler.runProcess].
     * 
     * Also log its arguments and output
     * when the corresponding logger console is available.
     */
    fun run(project: Project): ProcessOutput {
        val consoleHolder = when (this) {
            is RuffCommand -> project.ruffLogger
            is UVCommand -> project.uvLogger
            is RyeCommand -> project.ryeLogger
            is TyCommand -> project.redKnotLogger
            else -> null
        }
        
        consoleHolder?.debug("Running: ($workingDirectory) $this")
        
        return processHandler.runProcess(NO_TIME_LIMIT).also {
            consoleHolder?.debug("Output: ${ProcessOutputSurrogate(it)}")
            consoleHolder?.debug("")
        }
    }
    
    companion object {
        private const val NO_TIME_LIMIT = -1
    }
    
}
