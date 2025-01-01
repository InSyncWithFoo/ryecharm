package insyncwithfoo.ryecharm.ruff

import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.toHTML


internal fun RuffConfigurations.getFormattedTooltip(message: String, rule: String?): String {
    val formatted = tooltipFormat % Pair(message, rule)
    
    return when (renderTooltips) {
        true -> formatted.toHTML()
        else -> formatted
    }
}
