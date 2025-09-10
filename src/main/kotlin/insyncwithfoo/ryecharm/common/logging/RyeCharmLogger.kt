package insyncwithfoo.ryecharm.common.logging

import com.intellij.execution.process.ProcessOutput
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.toSurrogate
import insyncwithfoo.ryecharm.message


internal const val COMMAND_LOG_LINE_PREFIX = "Running"
internal const val OUTPUT_LOG_LINE_PREFIX = "Output"


internal class ConsoleHolder(private val project: Project, private val console: ConsoleView) : Disposable {
    
    init {
        Disposer.register(this, console)
    }
    
    override fun dispose() {}
    
    private fun print(message: Any, contentType: ConsoleViewContentType) {
        if (!project.isDisposed) {
            console.print("$message\n", contentType)
        }
    }
    
    fun log(message: String) = print(message, contentType = ConsoleViewContentType.NORMAL_OUTPUT)
    fun debug(message: String) = print(message, contentType = ConsoleViewContentType.LOG_DEBUG_OUTPUT)
    fun info(message: String) = print(message, contentType = ConsoleViewContentType.LOG_INFO_OUTPUT)
    fun warning(message: String) = print(message, contentType = ConsoleViewContentType.LOG_WARNING_OUTPUT)
    fun error(message: String) = print(message, contentType = ConsoleViewContentType.LOG_ERROR_OUTPUT)
    
}


/**
 * @see RyeCharmCommandOutputFoldingBuilder.getPlaceholderText
 */
internal fun ConsoleHolder.debug(command: Command) {
    debug("$COMMAND_LOG_LINE_PREFIX[${command.id}]: (${command.workingDirectory}) $command")
}


/**
 * @see RyeCharmCommandOutputFoldingBuilder.getPlaceholderText
 */
internal fun ConsoleHolder.debug(command: Command, output: ProcessOutput) {
    debug("$OUTPUT_LOG_LINE_PREFIX[${command.id}]: ${output.toSurrogate()}")
    debug("")
}


internal enum class ConsoleHolderKind(val tabName: String) {
    RUFF(message("toolWindows.ruff.tabName")),
    UV(message("toolWindows.uv.tabName")),
    RYE(message("toolWindows.rye.tabName")),
    TY(message("toolWindows.ty.tabName"));
}


@Service(Service.Level.PROJECT)
internal class RyeCharmLogger(private val project: Project) : Disposable {
    
    private val consoleHolders = mutableMapOf<ConsoleHolderKind, ConsoleHolder>()
    
    fun register(kind: ConsoleHolderKind, console: ConsoleView) {
        val holder = ConsoleHolder(project, console)
        
        Disposer.register(this, holder)
        consoleHolders[kind] = holder
    }
    
    fun getHolder(kind: ConsoleHolderKind) =
        consoleHolders[kind]
    
    override fun dispose() {
        consoleHolders.clear()
    }
    
}


internal val Project.pluginLogger: RyeCharmLogger
    get() = service<RyeCharmLogger>()


internal val Project.ruffLogger: ConsoleHolder?
    get() = pluginLogger.getHolder(ConsoleHolderKind.RUFF)


internal val Project.uvLogger: ConsoleHolder?
    get() = pluginLogger.getHolder(ConsoleHolderKind.UV)


internal val Project.ryeLogger: ConsoleHolder?
    get() = pluginLogger.getHolder(ConsoleHolderKind.RYE)


internal val Project.tyLogger: ConsoleHolder?
    get() = pluginLogger.getHolder(ConsoleHolderKind.TY)
