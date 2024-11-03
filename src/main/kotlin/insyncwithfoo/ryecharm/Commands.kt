package insyncwithfoo.ryecharm

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.io.toByteArray
import java.nio.CharBuffer
import java.nio.file.Path
import kotlin.io.path.nameWithoutExtension


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
        LOGGER.info("Running: ($workingDirectory) $this")
        
        return processHandler.runProcess(timeout).also {
            LOGGER.info("Output: ${ProcessOutputSurrogate(it)}")
        }
    }
    
    companion object {
        @JvmStatic
        protected val LOGGER = Logger.getInstance(Command::class.java)
    }
    
}
