package insyncwithfoo.ryecharm.ruff

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@RunWith(Parameterized::class)
internal class NoqaCommentWithoutCodeTest(
    @Suppress("unused")
    private val _name: String,
    private val text: String,
    private val code: String,
    private val expected: String
) {
    
    @Test
    fun test() {
        val comment = comment(text)
        
        assertEquals(expected, comment.withoutCode(code))
    }
    
    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data() = arrayOf(
            arrayOf("blanket", "# noqa", "A123", "# noqa"),
            arrayOf("no codes", "# noqa: ", "A123", "# noqa: "),
            arrayOf("has code - start", "# noqa: A123, B456 C789", "A123", "# noqa: B456 C789"),
            arrayOf("has code - middle", "# noqa: A123, B456 C789", "B456", "# noqa: A123 C789"),
            arrayOf("has code - end", "# noqa: A123, B456 C789", "C789", "# noqa: A123, B456"),
            arrayOf("duplicated code", "# noqa: A123, B456 C789 ,B456", "B456", "# noqa: A123 C789"),
            arrayOf("duplicated code - no other code", "# noqa: A123, A123,,A123", "A123", ""),
            arrayOf(
                "consecutive duplicated - start",
                "# noqa: A123, A123,,B456 C789 ,B456",
                "A123",
                "# noqa: B456 C789 ,B456"
            ),
            arrayOf(
                "consecutive duplicated - middle",
                "# noqa: A123, ,B456 ,B456   C789",
                "B456",
                "# noqa: A123   C789"
            ),
            arrayOf(
                "consecutive duplicated - end",
                "# noqa: A123, ,B456 A123,,,B456   C789 C789",
                "C789",
                "# noqa: A123, ,B456 A123,,,B456"
            )
        )
    }
    
}
