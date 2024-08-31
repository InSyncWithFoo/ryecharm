package insyncwithfoo.ryecharm

import kotlin.reflect.KClass


private val KClass<*>.qualifiedNameWithoutPackagePrefix: String
    get() = qualifiedName!!.removePrefix("insyncwithfoo.ryecharm.")


internal val KClass<*>.testDataPath: String
    get() = "src/test/testData/${qualifiedNameWithoutPackagePrefix.replace(".", "/")}"
