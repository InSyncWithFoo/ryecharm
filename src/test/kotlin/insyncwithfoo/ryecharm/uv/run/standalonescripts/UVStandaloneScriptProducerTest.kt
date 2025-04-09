package insyncwithfoo.ryecharm.uv.run.standalonescripts

import insyncwithfoo.ryecharm.PlatformTestCase
import org.junit.Test


internal class UVStandaloneScriptProducerTest : PlatformTestCase() {
    
    @Test
    fun `test getConfigurationFactory`() {
        UVStandaloneScriptProducer().configurationFactory
    }
    
}
