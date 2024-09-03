package insyncwithfoo.ryecharm.uv.commands

import insyncwithfoo.ryecharm.Command
import insyncwithfoo.ryecharm.configurations.uv.UVTimeouts
import insyncwithfoo.ryecharm.message


internal data class PythonInstallation(val name: String, val path: String? = null) {
    val downloadable: Boolean
        get() = path == null
}


internal class PythonListCommand : Command(), UVCommand {
    
    override val subcommand = "python"
    override val timeoutKey = UVTimeouts.PYTHON_LIST.key
    
    override val runningMessage: String
        get() = message("progresses.command.uv.pythonlist")
    
    companion object {
        // TODO: Move this to call site
        fun parseOutput(raw: String): List<PythonInstallation> {
            // FIXME:
            //   Paths with whitespace are simply ignored for now.
            //   This should only be refactored when uv provides the result in JSON.
            val line = """^(?<name>\S+)\s++(?:(?<path>\S+)|<download available>)$""".toRegex()
            
            val matches = line.findAll(raw)
            
            return matches.mapTo(mutableListOf()) {
                val (name, path) = it.destructured
                PythonInstallation(name, path.ifEmpty { null })
            }
        }
    }
    
}
