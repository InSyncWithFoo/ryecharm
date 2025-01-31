package insyncwithfoo.ryecharm.ruff

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters


private fun noCodes() = emptyList<String>()


@RunWith(Parameterized::class)
internal class NoqaCommentParseTest(
    @Suppress("unused")
    private val _name: String,
    private val text: String,
    private val start: Int?,
    private val end: Int?,
    private val codes: List<String>?
) {
    
    @Test
    fun test() {
        val comment = NoqaComment.parse(text)
        
        if (start != null) {
            assertEquals(start, comment!!.start)
        }
        
        if (end != null) {
            assertEquals(end, comment!!.end)
        }
        
        when (codes) {
            null -> assertEquals(null, comment)
            else -> assertEquals(codes, comment!!.codesAsStrings)
        }
    }
    
    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data() = arrayOf(
            arrayOf("line - invalid prefix", "# noq a", null, null, null),
            arrayOf("line - invalid colon", "# noqa :", 0, 6, noCodes()),
            arrayOf("line - no colon", "# noqa", 0, 6, noCodes()),
            arrayOf("line - casing", "# nOqA", 0, 6, noCodes()),
            arrayOf("line - colon", "# noqa:", 0, 7, noCodes()),
            arrayOf("line - colon and trailing space", "# noqa:  ", 0, 9, noCodes()),
            arrayOf("line - colon and trailing content", "# noqa:  # Foobar", 0, 7, noCodes()),
            arrayOf("line - leading content", "# lorem # noqa", 8, 14, noCodes()),
            arrayOf("line - trailing content", "# noqa # lorem", 0, 6, noCodes()),
            arrayOf("line - surrounding content", "# lorem # noqa # ipsum", 8, 14, noCodes()),
            arrayOf("line - codes", "# noqa: A123 B456C789", 0, 21, listOf("A123", "B456", "C789")),
            
            arrayOf("file - flake8", "# flake8: noqa", 0, 14, noCodes()),
            arrayOf("file - leading content", "# lorem # ruff: noqa", null, null, null),
            arrayOf("file - trailing content", "# ruff: noqa  # lorem", 0, 12, noCodes()),
            arrayOf("file - colon and trailing space", "# ruff: noqa:  ", 0, 15, noCodes()),
            arrayOf("file - colon and trailing content", "# ruff: noqa:  # lorem", 0, 13, noCodes()),
            arrayOf("file - codes", "# ruff: noqa : A123,B456  C789", 0, 30, listOf("A123", "B456", "C789")),
            arrayOf("file - bad separators 1", "# ruff: noqa: A123,,B456", 0, 18, listOf("A123")),
            arrayOf("file - bad separators 2", "# ruff: noqa: A123 ,B456", 0, 18, listOf("A123"))
        )
    }
    
}
