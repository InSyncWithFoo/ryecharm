package insyncwithfoo.ryecharm

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase
import java.nio.file.Path
import java.util.*


abstract class CommandFactoryTest : LightPlatformCodeInsightFixture4TestCase() {
    
    private val fixture by ::myFixture
    
    protected val lowercase: Char
        get() = ('a'..'z').random()
    protected val uppercase: Char
        get() = ('A'..'Z').random()
    protected val digit: Char
        get() = ('0'..'9').random()
    protected val ascii: Char
        get() = ('\u0000'..'\u00FF').random()
    
    protected val projectPath: Path?
        get() = project.path
    
    private fun randomPathFragment() =
        buildString((10..30).random()) {
            listOf(lowercase, uppercase, digit).random()
        }
    
    protected fun randomPath(): Path {
        val fragmentCount = (1..10).random()
        val fragments = buildList<String>(fragmentCount) {
            randomPathFragment()
        }
        
        return Path.of(fragments.joinToString("/"))
    }
    
    protected fun randomText() =
        buildString((0..10000).random()) { ascii }
    
    protected infix fun Arguments.include(subarguments: Arguments) =
        Collections.indexOfSubList(this, subarguments) != -1
    
    protected fun <T> T.orRandomlyNull() =
        this.takeIf { listOf(true, false).random() }
    
}
