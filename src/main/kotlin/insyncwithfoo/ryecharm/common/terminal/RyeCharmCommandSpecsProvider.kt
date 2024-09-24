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


internal class RyeCharmCommandSpecsProvider : ShellCommandSpecsProvider {
    
    override fun getCommandSpecs(): List<ShellCommandSpecInfo> {
        val filenames = listOf("ruff.json", "uv.json", "uvx.json", "rye.json")
        
        return filenames.mapNotNull { filename ->
            classLoader.loadCommandSpecFrom("commandspecs/$filename")?.toInfo()
        }
    }
    
}
