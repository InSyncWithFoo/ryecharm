package insyncwithfoo.ryecharm.ruff.hierarchy

import com.intellij.openapi.module.Module
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.python.hierarchy.call.PyCallHierarchyTreeStructureBase
import com.jetbrains.python.hierarchy.call.PyCalleeFunctionTreeStructure
import com.jetbrains.python.hierarchy.call.PyCallerFunctionTreeStructure
import com.jetbrains.python.psi.PyElement
import com.jetbrains.python.psi.PyFile
import insyncwithfoo.ryecharm.completedAbnormally
import insyncwithfoo.ryecharm.interpreterPath
import insyncwithfoo.ryecharm.isNormalPyFile
import insyncwithfoo.ryecharm.module
import insyncwithfoo.ryecharm.parseAsJSON
import insyncwithfoo.ryecharm.path
import insyncwithfoo.ryecharm.psiManager
import insyncwithfoo.ryecharm.ruff.commands.AnalyzeGraphDirection
import insyncwithfoo.ryecharm.ruff.commands.analyzeGraph
import insyncwithfoo.ryecharm.ruff.commands.ruff
import insyncwithfoo.ryecharm.runInBackground
import insyncwithfoo.ryecharm.toLocalVirtualFile
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.file.Path
import kotlin.io.path.div


private typealias Parent = Path
private typealias Children = List<Path>
private typealias ImportGraph = Map<Parent, Children>
private typealias MutableImportGraph = MutableMap<Parent, Children>


private class RelativePathSerializer(private val base: Path) : KSerializer<Path> {
    
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Path", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Path) {
        encoder.encodeString(value.relativize(base).toString())
    }
    
    override fun deserialize(decoder: Decoder): Path {
        return base / Path.of(decoder.decodeString())
    }
    
}


/**
 * @see PyCallerFunctionTreeStructure
 * @see PyCalleeFunctionTreeStructure
 */
internal class ImportGraphTreeStructure(file: PyFile, scopeType: String, private val direction: AnalyzeGraphDirection) :
    PyCallHierarchyTreeStructureBase(file.project, file, scopeType)
{
    
    private lateinit var cachedGraph: MutableImportGraph
    
    override fun getChildren(element: PyElement) =
        when (element is PyFile && element.isNormalPyFile) {
            true -> getChildren(element) ?: emptyMap()
            else -> emptyMap()
        }
    
    /**
     * Return a map of children nodes (e.g., modules imported)
     * to usage nodes (e.g., `import` statements for those modules).
     * 
     * Since `ruff analyze graph` doesn't say where the usages are,
     * the list of usage nodes are always empty.
     */
    private fun getChildren(file: PyFile): Map<PsiElement, Collection<PsiElement>>? {
        val project = file.project
        val module = file.module ?: return null
        
        val filePath = file.virtualFile.toNioPathOrNull() ?: return null
        
        val children = module.getChildrenOrQuery(filePath) ?: return null
        
        return children
            .mapNotNull { project.findPSIFile(it) }
            .associateWith { emptyList() }
    }
    
    private fun Project.findPSIFile(path: Path): PsiFile? {
        val virtualFile = path.toLocalVirtualFile() ?: return null
        return psiManager.findFile(virtualFile)
    }
    
    private fun Module.getChildrenOrQuery(file: Parent): Children? {
        if (!::cachedGraph.isInitialized) {
            cachedGraph = getImportGraph()?.toMutableMap() ?: return null
        }
        
        if (cachedGraph[file] != null) {
            return cachedGraph[file]
        }
        
        val graph = getImportGraph(file) ?: return null
        val children = graph[file] ?: return null
        
        return children.also { cachedGraph[file] = it }
    }
    
    private fun Module.getImportGraph(file: Path? = null): ImportGraph? {
        val projectPath = this.path ?: return null
        
        val ruff = project.ruff ?: return null
        val command = ruff.analyzeGraph(file, interpreterPath, direction)
        
        val output = runBlockingCancellable {
            project.runInBackground(command)
        }
        
        if (output.completedAbnormally) {
            return null
        }
        
        val pathSerializer = RelativePathSerializer(base = projectPath)
        val graphSerializer = MapSerializer(pathSerializer, ListSerializer(pathSerializer))
        
        return output.stdout.parseAsJSON<ImportGraph>(graphSerializer)
    }
    
}
