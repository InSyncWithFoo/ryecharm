package insyncwithfoo.ryecharm


private typealias PEP508Name = String


// https://peps.python.org/pep-0508/#names
internal val dependencySpecifierLookAlike = """(?i)^\s*(?<name>[A-Z0-9](?:[A-Z0-9._-]*[A-Z0-9])?).*""".toRegex()


// https://packaging.python.org/en/latest/specifications/name-normalization/#name-format
private val validName = "(?i)^([A-Z0-9]|[A-Z0-9][A-Z0-9._-]*[A-Z0-9])$".toRegex()


internal val String.isValidPEP508Name: Boolean
    get() = this.matches(validName)


// https://packaging.python.org/en/latest/specifications/name-normalization/#name-normalization
internal fun PEP508Name.pep508Normalize() =
    this.replace("[-_.]+".toRegex(), "-").lowercase()
