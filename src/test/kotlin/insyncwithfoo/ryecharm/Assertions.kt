package insyncwithfoo.ryecharm

import kotlin.test.assertNotEquals
import kotlin.test.assertTrue


@Suppress("NOTHING_TO_INLINE")
internal inline fun assertHasMessage(key: String) {
    assertNotEquals("!$key!", message(key))
}


internal inline fun <reified Supertype> assertSubtypeOf(subtype: Class<*>) {
    assertTrue(
        subtype.isSubtypeOf<Supertype>(),
        "`${subtype.canonicalName}` is not a subtype of `${Supertype::class.java.simpleName}`"
    )
}
