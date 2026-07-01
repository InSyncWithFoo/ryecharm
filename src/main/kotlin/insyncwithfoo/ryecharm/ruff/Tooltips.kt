package insyncwithfoo.ryecharm.ruff

import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.toHTML


internal fun RuffConfigurations.getFormattedTooltip(message: String, ruleName: String?, ruleCode: String?): String {
    val label = when (ruleCode != null && ruleName != null) {
        true -> "${ruleCode}: $ruleName"
        else -> ruleName ?: ruleCode
    }
    val uri = (ruleName ?: ruleCode)?.let { DocumentationURI.ruffRule(it) }
    
    val rulePossiblyWithLink = when (uri != null && renderTooltips) {
        true -> "[$label](${uri})"
        else -> label
    }
    val formatted = tooltipFormat % Pair(message, rulePossiblyWithLink)
    
    return when (renderTooltips) {
        true -> formatted.toHTML()
        else -> formatted
    }
}
