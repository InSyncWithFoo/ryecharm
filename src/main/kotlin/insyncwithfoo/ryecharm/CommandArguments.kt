package insyncwithfoo.ryecharm


internal class CommandArguments() {
    
    private var withoutParameters = mutableListOf<String>()
    private var withParameters = mutableMapOf<String, String>()
    
    constructor(vararg arguments: String) : this() {
        add(*arguments)
    }
    
    constructor(vararg arguments: Pair<String, String>) : this() {
        add(*arguments)
    }
    
    private fun add(vararg arguments: String) {
        withoutParameters.addAll(arguments)
    }
    
    private fun add(arguments: Iterable<String>) {
        withoutParameters.addAll(arguments)
    }
    
    private fun add(vararg arguments: Pair<String, String>) {
        arguments.forEach { (option, parameter) ->
            withParameters[option] = parameter
        }
    }
    
    operator fun plusAssign(parameters: Iterable<String>) {
        add(parameters)
    }
    
    operator fun plusAssign(parameter: String) {
        add(listOf(parameter))
    }
    
    operator fun set(option: String, parameter: String) {
        add(option to parameter)
    }
    
    operator fun contains(other: String) =
        other in withParameters || other in withoutParameters
    
    fun toList(): List<String> {
        return withoutParameters + withParameters.flatMap { listOfNotNull(it.key, it.value) }
    }
    
}
