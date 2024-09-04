package insyncwithfoo.ryecharm

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.IntentionActionDelegate


internal fun IntentionAction.getEventualDelegate(): IntentionAction =
    (this as? IntentionActionDelegate)?.delegate?.getEventualDelegate() ?: this
