package insyncwithfoo.ryecharm.configurations

import com.intellij.openapi.components.BaseState
import insyncwithfoo.ryecharm.message
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance


internal abstract class ConfigurationsTest<C : BaseState> {

    abstract val configurationClass: KClass<C>
    
    private lateinit var state: C
    private lateinit var fields: Map<String, KProperty1<C, *>>
    
    @Before
    fun setUp() {
        state = configurationClass.createInstance()
        fields = configurationClass.fields
    }
    
    protected fun doShapeTest(expectedSize: Int, action: C.() -> Unit) {
        assertEquals(expectedSize, fields.size)
        
        state.apply(action)
    }
    
    protected fun doMessagesTest(getKeyForName: (String) -> String) {
        val exemptions = listOf("executable")
        
        fields.forEach { (name, _) ->
            if (name in exemptions) {
                val key = getKeyForName(name)
                assertNotEquals("!$key!", message(key))
            }
        }
    }
    
}
