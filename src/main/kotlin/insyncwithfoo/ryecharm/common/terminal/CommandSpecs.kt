@file:Suppress("UnstableApiUsage")

package insyncwithfoo.ryecharm.common.terminal

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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
private const val MAX_SUGGESTION_PRIORITY = 100


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
    optionNode.description?.let { description(it) }
    
    if (optionNode.type != null) {
        argument(optionNode)
    }
}


private fun CommandNode.makeContentBuilder(suggestionPriority: SuggestionPriority): ShellCommandContext.() -> Unit = {
    priority = suggestionPriority
    
    description?.let { description(it) }
    arguments.forEach { argument(it) }
    options.forEach { option(it) }
    
    subcommands {
        subcommands.values.forEachIndexed { index, command ->
            subcommand(command, suggestionPriority = MAX_SUGGESTION_PRIORITY - index)
        }
    }
}


private fun ShellChildCommandsContext.subcommand(command: CommandNode, suggestionPriority: SuggestionPriority) =
    subcommand(command.name, content = command.makeContentBuilder(suggestionPriority))


private fun ShellCommandSpec(tree: CommandNode) =
    ShellCommandSpec(tree.name, tree.makeContentBuilder(MAX_SUGGESTION_PRIORITY))


private fun ClassLoader.loadCommandTreeFrom(path: String) =
    getResource(path)?.readText()?.let { Json.decodeFromString<CommandTreeAndVersion>(it) }


internal fun ClassLoader.loadCommandSpecFrom(path: String) =
    loadCommandTreeFrom(path)?.let { ShellCommandSpec(it.tree) }
