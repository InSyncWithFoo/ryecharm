package insyncwithfoo.ryecharm.ruff

import insyncwithfoo.ryecharm.lastModified
import kotlinx.serialization.Serializable
import java.nio.file.Path


internal typealias EpochTimestamp = Long


@Serializable
internal data class CachedResult<Result>(
    val result: Result,
    val executable: String,
    val timestamp: EpochTimestamp
) {
    
    constructor(result: Result, executable: Path) :
        this(result, executable.toString(), executable.lastModified)
    
    fun matches(executable: Path): Boolean {
        return this.executable == executable.toString() && timestamp == executable.lastModified
    }
    
}
