package insyncwithfoo.ryecharm

import java.nio.file.Path
import kotlin.test.assertContains


internal abstract class CommandFactoryTest(private val commandInterface: Class<*>) : PlatformTestCase() {
    
    protected val lowercase: Char
        get() = ('a'..'z').random()
    protected val uppercase: Char
        get() = ('A'..'Z').random()
    protected val digit: Char
        get() = ('0'..'9').random()
    protected val ascii: Char
        get() = ('\u0000'..'\u00FF').random()
    
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
    
    protected fun buildString(capacityRange: IntRange, generate: () -> Any) =
        buildString(capacityRange.random()) { append(generate()) }
    
    protected inline fun <reified C : Command> commandClassTest(subcommands: List<String>) {
        val instance = C::class.java.getConstructor().newInstance()
        
        assertInstanceOf(instance, commandInterface)
        assertEquals(subcommands, instance.subcommands)
    }
    
    protected inline fun <reified C : Command> commandTest(
        command: Command,
        stdin: String? = null,
        workingDirectory: Path? = project.path,
        block: CommandArgumentsTest.() -> Unit = {}
    ) {
        assertInstanceOf(command, C::class.java)
        
        assertEquals(workingDirectory, command.workingDirectory)
        assertEquals(stdin, command.stdin)
        
        CommandArgumentsTest(command.arguments).apply {
            block()
            assertNoUnaccountedArguments()
        }
    }
    
    protected class CommandArgumentsTest(original: CommandArguments) {
        
        private val remainingPositionalsAndFlags = original.positionalAndFlags.toMutableList()
        private val remainingNamedOptions = original.namedOptions.toMutableMap()
        
        fun assertArgumentsContain(vararg arguments: String) {
            for (argument in arguments) {
                assertContains(remainingPositionalsAndFlags, argument)
                remainingPositionalsAndFlags.remove(argument)
            }
        }
        
        fun assertArgumentsContain(argument: Pair<String, String>) {
            val (key, value) = argument
            
            assertContains(remainingNamedOptions, key)
            assertEquals(value, remainingNamedOptions[key])
            
            remainingNamedOptions.remove(key)
        }
        
        fun assertNoUnaccountedArguments() {
            assertEmpty(remainingPositionalsAndFlags)
            assertEmpty(remainingNamedOptions.toList())
        }
        
    }
    
}
