package insyncwithfoo.ryecharm

import com.intellij.psi.PsiElement
import org.toml.lang.psi.TomlElement
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.TomlKeySegment
import org.toml.lang.psi.TomlKeyValue
import org.toml.lang.psi.TomlLiteral
import org.toml.lang.psi.TomlTable
import org.toml.lang.psi.ext.TomlLiteralKind
import org.toml.lang.psi.ext.kind
import java.util.Collections


private fun <T> List<T>.startsWith(other: List<T>) =
    Collections.indexOfSubList(this, other) == 0


internal val TomlLiteral.isString: Boolean
    get() = kind is TomlLiteralKind.String


internal val TomlLiteral.stringContent: String?
    get() = (kind as? TomlLiteralKind.String)?.value


internal val TomlElement.keyValuePair: TomlKeyValue?
    get() = parent as? TomlKeyValue


internal val TomlTable.absoluteName: TOMLPath
    get() = TOMLPath(header.key?.text.orEmpty())


private val TomlKey.segmentedName: String
    get() = segments.joinToString(".") { it.text }


internal val TomlKey.table: TomlTable?
    get() = keyValuePair?.parent as? TomlTable


internal val TomlKey.absoluteName: TOMLPath
    get() = when {
        parent === containingFile -> TOMLPath(segmentedName)
        table == null -> TOMLPath(segmentedName)
        else -> table!!.absoluteName + TOMLPath(segmentedName)
    }


/**
 * Return either:
 *
 * * Itself (`this` is a [TomlKey])
 * * Its parent (`this` is possibly a [TomlKeySegment])
 * * Its grandparent (`this` is possibly a [PsiElement]`(BARE_KEY)`)
 * * `null`, when none of the above succeeds.
 */
internal val PsiElement.wrappingTomlKey: TomlKey?
    get() = this as? TomlKey
        ?: parent as? TomlKey
        ?: parent.parent as? TomlKey


internal val PsiElement.wrappingTomlLiteral: TomlLiteral?
    get() = this as? TomlLiteral
        ?: parent as? TomlLiteral


/**
 * Overly simplified abstraction of a TOML key or table's name.
 * 
 * The key or table represented can be absolute or relative.
 * Quoted fragments are deliberately left quoted.
 */
internal class TOMLPath private constructor(private val fragments: List<String>) {
    
    private val size: Int
        get() = fragments.size
    
    constructor(raw: String) : this(raw.split("."))
    
    operator fun plus(other: TOMLPath) =
        TOMLPath(this.fragments + other.fragments)
    
    override operator fun equals(other: Any?) =
        other is TOMLPath && this.fragments == other.fragments
    
    /**
     * If [other] is an ancestor of self,
     * return the difference between the two's [fragments].
     * Otherwise, return `null`.
     */
    private fun relativize(other: TOMLPath) = when {
        !this.fragments.startsWith(other.fragments) -> null
        else -> TOMLPath(fragments.drop(other.fragments.size))
    }
    
    fun relativize(other: String) =
        relativize(TOMLPath(other))
    
    /**
     * Whether [other] is the direct parent of self
     * (own [fragments] is the same as that of [other] plus one more).
     */
    infix fun isChildOf(other: TOMLPath) =
        this.relativize(other)?.size == 1
    
    infix fun isChildOf(other: String) =
        this isChildOf TOMLPath(other)
    
    override fun toString() =
        fragments.joinToString(".")
    
    override fun hashCode() =
        fragments.hashCode()
    
    companion object {
        fun listOf(vararg names: String) =
            names.map { TOMLPath(it) }
    }
    
}
