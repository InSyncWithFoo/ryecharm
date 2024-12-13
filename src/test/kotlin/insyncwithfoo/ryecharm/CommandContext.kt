package insyncwithfoo.ryecharm

import java.nio.file.Path


internal fun command(
    fragments: List<String>,
    workingDirectory: Path? = null,
    stdin: String? = null
): Command {
    val executable = fragments[0]
    val subcommand = fragments[1]
    val arguments = fragments.slice(2..<fragments.size)
    
    return object : Command() {
        
        override val subcommand = subcommand
        
        init {
            this.executable = executable.toPathOrNull()!!
            this.arguments = arguments
            this.workingDirectory = workingDirectory
            this.stdin = stdin
        }
        
    }
}


internal class CommandContext(private val workingDirectory: Path) {
    
    fun run(vararg fragments: String, stdin: String? = null) =
        command(fragments.toList(), workingDirectory, stdin).run()
    
}
