package insyncwithfoo.ryecharm.ruff.linting

import insyncwithfoo.ryecharm.ruff.OneBasedPinpoint
import insyncwithfoo.ryecharm.ruff.OneBasedRange


private fun SourceLocation.toOneBasedPinpoint() =
    OneBasedPinpoint(row, column)


internal val Ranged.oneBasedRange: OneBasedRange
    get() {
        val start = location.toOneBasedPinpoint()
        val end = endLocation.toOneBasedPinpoint()
        
        return OneBasedRange(start, end)
    }
