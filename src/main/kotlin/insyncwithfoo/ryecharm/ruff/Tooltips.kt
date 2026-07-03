package insyncwithfoo.ryecharm.ruff

import insyncwithfoo.ryecharm.DocumentationURI
import insyncwithfoo.ryecharm.configurations.ruff.RuffConfigurations
import insyncwithfoo.ryecharm.configurations.ruff.TooltipRuleFormat
import insyncwithfoo.ryecharm.toHTML


private fun RuffConfigurations.getRuleLabel(code: RuleCode?, name: String?) =
    when (tooltipRuleFormat) {
        TooltipRuleFormat.ONLY_CODE -> code
        TooltipRuleFormat.CODE_THEN_NAME -> code ?: name
        
        TooltipRuleFormat.ONLY_NAME -> name
        TooltipRuleFormat.NAME_THEN_CODE -> name ?: code
        
        TooltipRuleFormat.CODE_AND_NAME ->
            when (code != null && name != null) {
                true -> "${code}: $name"
                else -> name ?: code
            }
    }


internal fun RuffConfigurations.getFormattedTooltip(message: String, ruleName: String?, ruleCode: String?): String {
    val label = getRuleLabel(ruleCode, ruleName)
    val uri = (ruleName ?: ruleCode)?.let { DocumentationURI.ruffRule(it) }
    
    val isSyntaxError = label == DiagnosticID.InvalidSyntax.value
    val rulePossiblyWithLink = when (label == null || uri == null || isSyntaxError || !renderTooltips) {
        true -> label
        else -> "[$label](${uri})"
    }
    val formatted = tooltipFormat % Pair(message, rulePossiblyWithLink)
    
    return when (renderTooltips) {
        true -> formatted.toHTML()
        else -> formatted
    }
}
