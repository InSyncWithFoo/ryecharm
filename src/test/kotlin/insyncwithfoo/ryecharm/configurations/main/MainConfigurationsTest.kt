package insyncwithfoo.ryecharm.configurations.main

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class MainConfigurationsTest : ConfigurationsTest<MainConfigurations>() {
    
    override val configurationClass = MainConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 6) {
            assertEquals(true, languageInjectionPEP723Blocks)
            assertEquals(true, languageInjectionRequirements)
            
            assertEquals(true, suppressIncorrectNIRI)
            assertEquals(false, suppressIncorrectNIRINonUVSDK)
            
            assertEquals(true, consoleFilterRuffAndTYPaths)
            assertEquals(false, resolveRuffTYPathsAgainstSourceRoots)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.main.$name.label" }
    }
    
}
