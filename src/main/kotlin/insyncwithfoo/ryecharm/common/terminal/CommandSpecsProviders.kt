package insyncwithfoo.ryecharm.common.terminal

import com.intellij.terminal.completion.spec.ShellCommandSpec
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpec
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecConflictStrategy
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecInfo
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpecsProvider


@Suppress("UnstableApiUsage")
private val fruffCommandSpec: ShellCommandSpec
    get() = ShellCommandSpec("ruff") {
        requiresSubcommand = false
        
        argument {
            displayName("foo")
            suggestions("A", "B", "C")
            
            isOptional = false
            isVariadic = false
        }
        
        option("-a", "--abc") {
            description("")
            
            insertValue = ""
            priority = (0..100).random()
            
            isRequired = false
            repeatTimes = 0
            displayName = "abc"
            separator = "="  // " "
            
            argument { /* ... */ }
        }
        
        subcommands {
            subcommand("a", "b", "c")
            
            subcommand("d") {
                requiresSubcommand = false
                
                argument { /* ... */ }
                
                option("") {
                    // ...
                    argument { /* ... */ }
                }
            }
        }
    }


@Suppress("UnstableApiUsage")
internal class RuffCommandSpecsProvider : ShellCommandSpecsProvider {
    
    override fun getCommandSpecs(): List<ShellCommandSpecInfo> {
        val conflictStrategy = ShellCommandSpecConflictStrategy.REPLACE
        val commandSpec = this::class.java.classLoader.loadCommandSpecFrom("commandspecs/ruff.json")
            ?: return emptyList()
        
        return listOf(ShellCommandSpecInfo.create(commandSpec, conflictStrategy))
    }
    
}


@Suppress("UnstableApiUsage")
internal class UVCommandSpecsProvider : ShellCommandSpecsProvider {
    
    override fun getCommandSpecs(): List<ShellCommandSpecInfo> {
        val conflictStrategy = ShellCommandSpecConflictStrategy.REPLACE
        val commandSpec = this::class.java.classLoader.loadCommandSpecFrom("commandspecs/uv.json")
            ?: return emptyList()
        
        return listOf(ShellCommandSpecInfo.create(commandSpec, conflictStrategy))
    }
    
}
