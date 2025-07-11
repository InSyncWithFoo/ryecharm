package insyncwithfoo.ryecharm.configurations.uv

import insyncwithfoo.ryecharm.configurations.ConfigurationsTest
import org.junit.Assert.assertEquals
import org.junit.Test


internal class UVConfigurationsTest : ConfigurationsTest<UVConfigurations>() {
    
    override val configurationClass = UVConfigurations::class
    
    @Test
    fun `test shape`() {
        doShapeTest(expectedSize = 11) {
            assertEquals(null, executable)
            assertEquals(null, configurationFile)
            
            assertEquals(true, showDependencyTreesOnHover)
            assertEquals(true, showVersionSpecifiersForDependencies)
            assertEquals(false, showLatestVersionsForDependencies)
            assertEquals(true, dedupeDependencyTrees)
            assertEquals(255, dependencyTreeDepth)
            assertEquals(false, showInvertedDependencyTreeFirst)
            
            assertEquals(UpdateMethod.NOTIFY, updateMethod)
            
            assertEquals(false, retrieveDependenciesInReadAction)
            assertEquals(5, dependenciesDataMaxAge)
        }
    }
    
    @Test
    fun `test messages`() {
        doMessagesTest { name -> "configurations.uv.$name.label" }
    }
    
}
