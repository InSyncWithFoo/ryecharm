package insyncwithfoo.ryecharm.uv.run.projectscripts

import insyncwithfoo.ryecharm.absoluteName
import insyncwithfoo.ryecharm.isPyprojectToml
import org.toml.lang.psi.TomlFile
import org.toml.lang.psi.TomlKey
import org.toml.lang.psi.ext.name


internal val TomlKey.projectScriptName: String?
    get() {
        val file = containingFile as? TomlFile ?: return null
        
        when {
            file.virtualFile?.isPyprojectToml != true -> return null
            !(absoluteName isChildOf "project.scripts") -> return null
        }
        
        return name
    }
