@file:Suppress("UnstableApiUsage")

package insyncwithfoo.ryecharm.common.terminal

import insyncwithfoo.ryecharm.parseAsJSON
import insyncwithfoo.ryecharm.toHTML
import kotlinx.serialization.Serializable
import org.jetbrains.plugins.terminal.block.completion.spec.ShellCommandSpec
import org.jetbrains.plugins.terminal.block.completion.spec.dsl.ShellArgumentContext
import org.jetbrains.plugins.terminal.block.completion.spec.dsl.ShellChildCommandsContext
import org.jetbrains.plugins.terminal.block.completion.spec.dsl.ShellCommandContext
import org.jetbrains.plugins.terminal.block.completion.spec.dsl.ShellOptionContext
import org.jetbrains.plugins.terminal.block.completion.spec.dsl.ShellSuggestionContext


/**
 * A value to be passed to [ShellSuggestionContext.priority].
 */
private typealias SuggestionPriority = Int

private typealias ContentBuilder<Context> = Context.() -> Unit
private typealias ArgumentBuilder = (ContentBuilder<ShellArgumentContext>) -> Unit


/**
 * @see SuggestionPriority
 */
private val SUGGESTION_PRIORITY_RANGE = 0..100


@Serializable
private data class OptionOrArgumentNode(
    val name: String,
    val alias: String?,
    val variadic: Boolean,
    val optional: Boolean,
    val type: String?,
    val suggestions: List<String>?,
    val description: String?
)


@Serializable
private data class CommandNode(
    val name: String,
    val path: List<String>,
    val description: String?,
    val arguments: List<OptionOrArgumentNode>,
    val options: List<OptionOrArgumentNode>,
    val subcommands: Map<String, CommandNode>
)


@Serializable
private data class CommandTreeAndVersion(
    val version: String,
    val tree: CommandNode
)


private val OptionOrArgumentNode.names: List<String>
    get() = listOfNotNull(name, alias)


private fun ShellArgumentContext.suggestions(names: List<String>) {
    suggestions(*names.toTypedArray())
}


private fun ArgumentBuilder.fromNode(node: OptionOrArgumentNode) = invoke {
    displayName(node.name.removePrefix("--"))
    node.suggestions?.let { suggestions(it) }
    
    isVariadic = node.variadic
    isOptional = node.optional
}


private fun ShellCommandContext.argument(argumentNode: OptionOrArgumentNode) =
    ::argument.fromNode(argumentNode)


private fun ShellOptionContext.argument(argumentNode: OptionOrArgumentNode) =
    ::argument.fromNode(argumentNode)


private fun ShellCommandContext.option(names: List<String>, content: ShellOptionContext.() -> Unit) =
    option(*names.toTypedArray(), content = content)


private fun ShellCommandContext.option(optionNode: OptionOrArgumentNode) = option(optionNode.names) {
    optionNode.description?.let { description(it.toHTML()) }
    
    if (optionNode.type != null) {
        argument(optionNode)
    }
}


private fun CommandNode.makeContentBuilder(suggestionPriority: SuggestionPriority): ShellCommandContext.() -> Unit = {
    priority = suggestionPriority.coerceIn(SUGGESTION_PRIORITY_RANGE)
    
    description?.let { description(it.toHTML()) }
    arguments.forEach { argument(it) }
    options.forEach { option(it) }
    
    subcommands {
        subcommands.values.forEachIndexed { index, command ->
            subcommand(command, suggestionPriority = SUGGESTION_PRIORITY_RANGE.last - index)
        }
    }
}


private fun ShellChildCommandsContext.subcommand(command: CommandNode, suggestionPriority: SuggestionPriority) =
    subcommand(command.name, content = command.makeContentBuilder(suggestionPriority))


private fun ShellCommandSpec(tree: CommandNode) =
    ShellCommandSpec(tree.name, tree.makeContentBuilder(SUGGESTION_PRIORITY_RANGE.last))


private fun ClassLoader.loadCommandTreeFrom(path: String) =
    getResource(path)?.readText()?.parseAsJSON<CommandTreeAndVersion>()


internal fun ClassLoader.loadCommandSpecFrom(path: String) =
    loadCommandTreeFrom(path)?.let { ShellCommandSpec(it.tree) }
