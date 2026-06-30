package insyncwithfoo.ryecharm

import kotlin.reflect.KClass


internal val ClassLoader.id: Int
    get() = System.identityHashCode(this)


internal val KClass<*>.loaderID: Int
    get() = java.classLoader.id
