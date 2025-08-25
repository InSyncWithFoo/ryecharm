package insyncwithfoo.ryecharm.ruff.hierarchy

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
    
    private val importGraph by lazy {
        file.project.getImportGraph()
    }
    
    override fun getChildren(element: PyElement) =
        when (element is PyFile && element.isNormalPyFile) {
            true -> getChildren(element) ?: emptyMap()
            else -> emptyMap()
        }
    
    private fun getChildren(file: PyFile): Map<PsiElement, Collection<PsiElement>>? {
        val project = file.project
        val filePath = file.virtualFile.toNioPathOrNull() ?: return null
        val importGraph = this.importGraph ?: return null
        
        // TODO: Query non-project files lazily
        val children = importGraph[filePath] ?: return emptyMap()
        val childrenToGrandchildren = mutableMapOf<PsiElement, List<PsiElement>>()
        
        for (child in children) {
            val grandchildren = importGraph[child] ?: emptyList()
            
            val childFile = project.findPSIFile(child) ?: continue
            val grandchildrenFiles = grandchildren.mapNotNull { project.findPSIFile(it) }
            
            childrenToGrandchildren[childFile] = grandchildrenFiles
        }
        
        return childrenToGrandchildren
    }
    
    private fun Project.findPSIFile(path: Path): PsiFile? {
        val virtualFile = path.toLocalVirtualFile() ?: return null
        return psiManager.findFile(virtualFile)
    }
    
    private fun Project.getImportGraph(): ImportGraph? {
        val projectPath = this.path ?: return null
        
        val ruff = this.ruff ?: return null
        val command = ruff.analyzeGraph(interpreterPath, direction)
        
        val output = runBlockingCancellable {
            runInBackground(command)
        }
        
        if (output.completedAbnormally) {
            return null
        }
        
        val pathSerializer = RelativePathSerializer(base = projectPath)
        val graphSerializer = MapSerializer(pathSerializer, ListSerializer(pathSerializer))
        
        return output.stdout.parseAsJSON<ImportGraph>(graphSerializer)
    }
    
}
