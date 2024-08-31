package insyncwithfoo.ryecharm.uv.managing

import com.intellij.execution.ExecutionException
import com.intellij.execution.process.ProcessOutput
import com.intellij.webcore.packaging.PackageManagementService
import com.jetbrains.python.packaging.bridge.PythonPackageManagementServiceBridge
import insyncwithfoo.ryecharm.ProcessOutputSurrogate


/**
 * The error to be used in [UVPackageManager]'s methods.
 * Its message is a JSON representation of the given [ProcessOutput].
 * 
 * It is passed to [PackageManagementService.Listener]s
 * by [PythonPackageManagementServiceBridge.installPackage].
 */
internal class UVReportedError(output: ProcessOutputSurrogate) : ExecutionException(output.toString()) {
    
    constructor(output: ProcessOutput) : this(ProcessOutputSurrogate(output))
    
}
