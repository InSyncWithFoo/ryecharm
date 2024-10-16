package insyncwithfoo.ryecharm.others.dependencygroups

import org.toml.lang.psi.TomlInlineTable
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.TomlTable
import org.toml.lang.psi.ext.name


internal typealias DependencyGroupsTable = TomlTable
internal typealias IncludeGroupTable = TomlInlineTable
internal typealias GroupNameString = TomlLiteral


internal val TomlTable.isDependencyGroups: Boolean
    get() = header.key?.name == "dependency-groups"


internal val DependencyGroupsTable.groupKeys: List<TomlKey>
    get() = entries.map { it.key }


internal val DependencyGroupsTable.groupNames: List<String>
    get() = groupKeys.mapNotNull { it.name?.normalize() }
