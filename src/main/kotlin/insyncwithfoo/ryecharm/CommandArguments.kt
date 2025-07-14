package insyncwithfoo.ryecharm

import org.jetbrains.annotations.VisibleForTesting


internal class CommandArguments() {
    
    private val _positionalsAndFlags = mutableListOf<String>()
    private val _namedOptions = mutableMapOf<String, String>()
    
    @get:VisibleForTesting
    val positionalAndFlags: List<String>
        get() = _positionalsAndFlags
    
    @get:VisibleForTesting
    val namedOptions: Map<String, String>
        get() = _namedOptions
    
    constructor(vararg arguments: String) : this() {
        add(*arguments)
    }
    
    constructor(vararg arguments: Pair<String, String>) : this() {
        add(*arguments)
    }
    
    private fun add(vararg arguments: String) {
        _positionalsAndFlags.addAll(arguments)
    }
    
    private fun add(arguments: Iterable<String>) {
        _positionalsAndFlags.addAll(arguments)
    }
    
    private fun add(vararg arguments: Pair<String, String>) {
        arguments.forEach { (option, parameter) ->
            _namedOptions[option] = parameter
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
        other in _namedOptions || other in _positionalsAndFlags
    
    fun toList() =
        _positionalsAndFlags + _namedOptions.flatMap { listOf(it.key, it.value) }
    
}
