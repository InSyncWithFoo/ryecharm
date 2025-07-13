package insyncwithfoo.ryecharm

import java.nio.file.Path
import java.util.Collections
import kotlin.test.assertContains


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
    
    protected inline fun <reified C : Command> commandClassTest(subcommands: List<String>) {
        val instance = C::class.java.getConstructor().newInstance()
        
        assertInstanceOf(instance, commandInterface)
        assertEquals(subcommands, instance.subcommands)
    }
    
    protected inline fun <reified C : Command> commandTest(
        command: Command,
        stdin: String? = null,
        workingDirectory: Path? = project.path
    ) {
        assertInstanceOf(command, C::class.java)
        
        assertEquals(workingDirectory, command.workingDirectory)
        assertEquals(stdin, command.stdin)
    }
    
    protected inline fun <reified C : Command> commandTest(
        command: Command,
        stdin: String? = null,
        workingDirectory: Path? = project.path,
        block: CommandArgumentsTest.() -> Unit
    ) {
        commandTest<C>(command, stdin, workingDirectory)
        
        CommandArgumentsTest(command.arguments).apply {
            block()
            assertNoUnaccountedArguments()
        }
    }
    
    protected class CommandArgumentsTest(original: List<String>) {
        
        private val remaining = original.toMutableList()
        
        fun assertArgumentsContain(vararg arguments: String) {
            for (argument in arguments) {
                val index = remaining.indexOf(argument)
                
                assertContains(remaining.indices, index)
                remaining.removeAt(index)
            }
        }
        
        fun assertArgumentsContain(argument: Pair<String, String>) {
            val (key, value) = argument
            
            val keyIndex = remaining.indexOf(key)
            val valueIndex = keyIndex + 1
            
            assertContains(remaining.indices, keyIndex)
            assertContains(remaining.indices, valueIndex)
            assertEquals(value, remaining[valueIndex])
            
            remaining.subList(keyIndex, valueIndex + 1).clear()
        }
        
        fun assertNoUnaccountedArguments() {
            assertEmpty(remaining)
        }
        
    }
    
}
