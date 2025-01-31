package insyncwithfoo.ryecharm.ruff

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@RunWith(Parameterized::class)
internal class NoqaCommentWithNewCodeTest(
    @Suppress("unused")
    private val _name: String,
    private val text: String,
    private val code: String,
    private val expected: String
) {
    
    @Test
    fun test() {
        val comment = comment(text)
        
        assertEquals(expected, comment.withNewCode(code))
    }
    
    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data() = arrayOf(
            arrayOf("blanket", "#noqa", "A123", "#noqa: A123"),
            arrayOf("no codes", "#noqa:", "A123", "#noqa: A123"),
            arrayOf("no codes and trailing spaces", "#noqa:  ", "A123", "#noqa:  A123"),
            arrayOf("no codes and trailing content", "#noqa:  # Foo", "A123", "#noqa: A123"),
            arrayOf("no separator", "# noqa: A123", "B456", "# noqa: A123, B456"),
            arrayOf("one separator", "# noqa: A123,B456", "C789", "# noqa: A123,B456,C789"),
            arrayOf("multiple separators", "# noqa: A123,B456 C789", "D012", "# noqa: A123,B456 C789 D012")
        )
    }
    
}
