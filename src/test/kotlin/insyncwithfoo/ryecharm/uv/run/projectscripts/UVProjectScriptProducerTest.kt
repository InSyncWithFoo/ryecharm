package insyncwithfoo.ryecharm.uv.run.projectscripts

import insyncwithfoo.ryecharm.PlatformTestCase
import org.junit.Test


internal class UVProjectScriptProducerTest : PlatformTestCase() {
    
    @Test
    fun `test getConfigurationFactory`() {
        UVProjectScriptProducer().configurationFactory
    }
    
}
