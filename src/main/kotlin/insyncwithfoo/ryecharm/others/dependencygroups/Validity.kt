package insyncwithfoo.ryecharm.others.dependencygroups


private typealias GroupName = String


// https://packaging.python.org/en/latest/specifications/name-normalization/#name-format
private val validGroupName = "(?i)^([A-Z0-9]|[A-Z0-9][A-Z0-9._-]*[A-Z0-9])$".toRegex()


internal val GroupName.isValid: Boolean
    get() = this.matches(validGroupName)


// https://packaging.python.org/en/latest/specifications/name-normalization/#name-normalization
internal fun GroupName.normalize() =
    this.replace("[-_.]+".toRegex(), "-").lowercase()
