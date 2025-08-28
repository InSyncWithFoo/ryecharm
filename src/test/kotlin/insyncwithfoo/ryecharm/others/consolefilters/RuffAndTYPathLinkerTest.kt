package insyncwithfoo.ryecharm.others.consolefilters

import org.junit.Test


internal class RuffAndTYPathLinkerTest : ConsoleFilterTest() {
    
    override val filter: RuffAndTYPathLinker
        get() = RuffAndTYPathLinker(project)
    
    @Test
    fun `test existing file`() = fileBasedTest("foo.py") {
        filterTest(
            before = """
                |F811 Redefinition of unused `foo` from line 1
                | --> foo.py:6:5
                |  |
                |6 | def foo(): ...
                |  |     ^^^ `foo` redefined here
                |  |
                | ::: foo.py:1:5
                |  |
                |1 | def foo(): ...
                |  |     --- previous definition of `foo` here
                |  |
                |help: Remove definition: `foo`
                |
                |Found 1 error.
            """.trimMargin(),
            after = """
                |F811 Redefinition of unused `foo` from line 1
                | --> [foo.py:6:5 -> foo.py]
                |  |
                |6 | def foo(): ...
                |  |     ^^^ `foo` redefined here
                |  |
                | ::: [foo.py:1:5 -> foo.py]
                |  |
                |1 | def foo(): ...
                |  |     --- previous definition of `foo` here
                |  |
                |help: Remove definition: `foo`
                |
                |Found 1 error.
            """.trimMargin()
        )
    }
    
    @Test
    fun `test stdin file`() = fileBasedTest("foo.py") {
        filterTest(
            before = """
                |F401 [*] `os` imported but unused
                | --> -:1:8
                |  |
                |1 | import os
                |  |        ^^
                |  |
                |help: Remove unused import: `os`
                |
                |Found 1 error.
                |[*] 1 fixable with the `--fix` option.
            """.trimMargin(),
            after = """
                |F401 [*] `os` imported but unused
                | --> [-:1:8 -> null]
                |  |
                |1 | import os
                |  |        ^^
                |  |
                |help: Remove unused import: `os`
                |
                |Found 1 error.
                |[*] 1 fixable with the `--fix` option.
            """.trimMargin()
        )
    }
    
    @Test
    fun `test deeper indentation`() = fileBasedTest("foo.py") {
        filterTest(
            before = """
                |error[no-matching-overload]: No overload of function `foo` matches arguments                                                                                     
                |  --> foo.py:12:1
                |   |
                |12 | foo()
                |   | ^^^^^
                |   |
                |info: First overload defined here
                | --> foo.py:5:5
                |  |
                |4 | @overload
                |5 | def foo(v: int) -> None: ...
                |  |     ^^^^^^^^^^^^^^^^^^^
                |6 | @overload
                |7 | def foo(v: str) -> None: ...
                |  |
                |info: Possible overloads for function `foo`:
                |info:   (v: int) -> None
                |info:   (v: str) -> None
                |info: Overload implementation defined here
                | --> foo.py:9:5
                |  |
                |7 | def foo(v: str) -> None: ...
                |8 |
                |9 | def foo(v: int | str) -> None: ...
                |  |     ^^^^^^^^^^^^^^^^^^^^^^^^^
                |  |
                |info: rule `no-matching-overload` is enabled by default
                |
                |Found 1 diagnostic
            """.trimMargin(),
            after = """
                |error[no-matching-overload]: No overload of function `foo` matches arguments                                                                                     
                |  --> [foo.py:12:1 -> foo.py]
                |   |
                |12 | foo()
                |   | ^^^^^
                |   |
                |info: First overload defined here
                | --> [foo.py:5:5 -> foo.py]
                |  |
                |4 | @overload
                |5 | def foo(v: int) -> None: ...
                |  |     ^^^^^^^^^^^^^^^^^^^
                |6 | @overload
                |7 | def foo(v: str) -> None: ...
                |  |
                |info: Possible overloads for function `foo`:
                |info:   (v: int) -> None
                |info:   (v: str) -> None
                |info: Overload implementation defined here
                | --> [foo.py:9:5 -> foo.py]
                |  |
                |7 | def foo(v: str) -> None: ...
                |8 |
                |9 | def foo(v: int | str) -> None: ...
                |  |     ^^^^^^^^^^^^^^^^^^^^^^^^^
                |  |
                |info: rule `no-matching-overload` is enabled by default
                |
                |Found 1 diagnostic
            """.trimMargin()
        )
    }
    
    @Test
    fun `test no indentation`() = fileBasedTest("foo.py") {
        filterTest(
            before = """
                |E902 The system cannot find the file specified. (os error 2)
                |--> foo.py:1:1
                |
                |Found 1 error.
            """.trimMargin(),
            after = """
                |E902 The system cannot find the file specified. (os error 2)
                |--> [foo.py:1:1 -> foo.py]
                |
                |Found 1 error.
            """.trimMargin()
        )
    }
    
    @Test
    fun `test non-existent file`() = fileBasedTest("foo.py") {
        filterTest(
            before = """
                |error[unresolved-import]: Cannot resolve imported module `a_n_plus_b`
                | --> foo/bar.py:1:6
                |  |
                |1 | from a_n_plus_b import n
                |  |      ^^^^^^^^^^
                |  |
                |info: make sure your Python environment is properly configured: https://docs.astral.sh/ty/modules/#python-environment
                |info: rule `unresolved-import` is enabled by default
                |
                |Found 1 diagnostic
            """.trimMargin(),
            after = """
                |error[unresolved-import]: Cannot resolve imported module `a_n_plus_b`
                | --> foo/bar.py:1:6
                |  |
                |1 | from a_n_plus_b import n
                |  |      ^^^^^^^^^^
                |  |
                |info: make sure your Python environment is properly configured: https://docs.astral.sh/ty/modules/#python-environment
                |info: rule `unresolved-import` is enabled by default
                |
                |Found 1 diagnostic
            """.trimMargin()
        )
    }
    
}
