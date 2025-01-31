package insyncwithfoo.ryecharm.ruff

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


@RunWith(Parameterized::class)
internal class NoqaCommentFindCodeAtOffsetTest(
    @Suppress("unused")
    private val _name: String,
    private val text: String,
    private val offset: Int,
    private val expected: String?
) {
    
    @Test
    fun test() {
        val comment = comment(text)
        
        assertEquals(expected, comment.findCodeAtOffset(offset))
    }
    
    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data() = arrayOf(
            arrayOf("start", "# noqa: A123, B456", 14, "B456"),
            arrayOf("middle", "# noqa: A123, B456", 15, "B456"),
            arrayOf("end", "# noqa: A123, B456", 12, "A123"),
            arrayOf("no separator", "# noqa: A123B456", 12, "A123"),
            arrayOf("before", "# noqa: A123B456", 5, null)
        )
    }
    
}
