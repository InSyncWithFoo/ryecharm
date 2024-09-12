package insyncwithfoo.ryecharm.common.terminal

import com.intellij.terminal.completion.spec.ShellCommandSpec
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecConflictStrategy
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecInfo
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecsProvider


private inline val Any.classLoader: ClassLoader
    get() = this::class.java.classLoader


@Suppress("UnstableApiUsage")
private fun ShellCommandSpec.toInfo() =
    ShellCommandSpecInfo.create(this, ShellCommandSpecConflictStrategy.REPLACE)


@Suppress("UnstableApiUsage")
internal class RuffCommandSpecsProvider : ShellCommandSpecsProvider {
    
    override fun getCommandSpecs(): List<ShellCommandSpecInfo> {
        val commandSpecInfo = classLoader.loadCommandSpecFrom("commandspecs/ruff.json")?.toInfo()
        
        return listOfNotNull(commandSpecInfo)
    }
    
}


@Suppress("UnstableApiUsage")
internal class UVCommandSpecsProvider : ShellCommandSpecsProvider {
    
    override fun getCommandSpecs(): List<ShellCommandSpecInfo> {
        val commandSpecInfo = classLoader.loadCommandSpecFrom("commandspecs/uv.json")?.toInfo()
        
        return listOfNotNull(commandSpecInfo)
    }
    
}
