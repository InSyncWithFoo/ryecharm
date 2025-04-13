package insyncwithfoo.ryecharm.ruff

import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.toHTML


internal fun RuffConfigurations.getFormattedTooltip(message: String, rule: RuleCode?): String {
    val rulePossiblyWithLink = when (rule != null && renderTooltips) {
        true -> "[$rule](${DocumentationURI.ruffRule(rule)})"
        else -> rule
    }
    val formatted = tooltipFormat % Pair(message, rulePossiblyWithLink)
    
    return when (renderTooltips) {
        true -> formatted.toHTML()
        else -> formatted
    }
}
