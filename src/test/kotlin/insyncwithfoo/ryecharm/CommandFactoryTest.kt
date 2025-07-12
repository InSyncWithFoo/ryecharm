package insyncwithfoo.ryecharm

import java.nio.file.Path
import java.util.Collections


private typealias Arguments = List<String>


internal abstract class CommandFactoryTest(private val commandInterface: Class<*>) : PlatformTestCase() {
    
    protected val lowercase: Char
        get() = ('a'..'z').random()
    protected val uppercase: Char
        get() = ('A'..'Z').random()
    protected val digit: Char
        get() = ('0'..'9').random()
    protected val ascii: Char
        get() = ('\u0000'..'\u00FF').random()
    
    protected val boolean: Boolean
        get() = listOf(true, false).random()
    
    private fun randomPathFragment() =
        buildString(10..30) {
            listOf(lowercase, uppercase, digit).random()
        }
    
    protected fun randomPath(): Path {
        val fragmentCount = (1..10).random()
        val fragments = buildList(fragmentCount) {
            add(randomPathFragment())
        }
        
        return Path.of(fragments.joinToString("/"))
    }
    
    protected fun randomText() =
        buildString(0..10000) { ascii }
    
    protected infix fun Arguments.include(subarguments: Arguments) =
        Collections.indexOfSubList(this, subarguments) != -1
    
    protected fun <T> T.orRandomlyNull() =
        this.takeIf { boolean }
    
    protected fun buildString(capacityRange: IntRange, generate: () -> Any) =
        buildString(capacityRange.random()) { append(generate()) }
    
    protected inline fun <reified T : Command> commandTest(
        command: Command,
        subcommands: List<String>,
        arguments: List<String> = emptyList(),
        workingDirectory: Path? = project.path,
        noinline block: (Command.() -> Unit)? = null
    ) {
        assertInstanceOf(command, commandInterface)
        assertInstanceOf(command, T::class.java)
        
        assertEquals(subcommands, command.subcommands)
        assertEquals(arguments, command.arguments)
        assertEquals(workingDirectory, command.workingDirectory)
        
        block?.let { command.apply(it) }
    }
    
}
