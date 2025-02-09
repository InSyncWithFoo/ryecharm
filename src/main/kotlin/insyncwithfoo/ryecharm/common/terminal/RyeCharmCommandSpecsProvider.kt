@file:Suppress("UnstableApiUsage")

package insyncwithfoo.ryecharm.common.terminal

import com.intellij.terminal.completion.spec.ShellCommandSpec
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecConflictStrategy
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecInfo
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecsProvider


private inline val Any.classLoader: ClassLoader
    get() = this::class.java.classLoader


private fun ShellCommandSpec.toInfo() =
    ShellCommandSpecInfo.create(this, ShellCommandSpecConflictStrategy.REPLACE)


/**
 * Provide command specs for `ruff`, `uv`, `uvx` and `rye`
 * in the new terminal.
 * 
 * The information is read from bundled JSON files
 * (generated using `scripts/command_specs.py`)
 * and then reconstructed via [makeContentBuilder].
 * 
 * The exact format is decided by the script.
 * It is replicated here as [CommandTreeAndVersion],
 * [CommandNode] and [OptionOrArgumentNode].
 */
internal class RyeCharmCommandSpecsProvider : ShellCommandSpecsProvider {
    
    override fun getCommandSpecs(): List<ShellCommandSpecInfo> {
        val filenames = listOf("ruff.json", "uv.json", "uvx.json", "rye.json")
        
        return filenames.mapNotNull { filename ->
            classLoader.loadCommandSpecFrom("commandspecs/$filename")?.toInfo()
        }
    }
    
}
