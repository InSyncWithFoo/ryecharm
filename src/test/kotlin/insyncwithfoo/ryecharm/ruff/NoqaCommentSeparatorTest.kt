package insyncwithfoo.ryecharm.ruff

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@RunWith(Parameterized::class)
internal class NoqaCommentSeparatorTest(
    @Suppress("unused")
    private val _name: String,
    private val text: String,
    private val expected: String
) {
    
    @Test
    fun test() {
        val comment = comment(text)
        
        assertEquals(expected, comment.separator)
    }
    
    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data() = arrayOf(
            arrayOf("no codes", "# noqa", ", "),
            arrayOf("one code", "# noqa: A123", ", "),
            arrayOf("one separator", "# noqa: A123,B456", ","),
            arrayOf("multiple separators", "# noqa: A123,B456 ,C789", " ,")
        )
    }
    
}
