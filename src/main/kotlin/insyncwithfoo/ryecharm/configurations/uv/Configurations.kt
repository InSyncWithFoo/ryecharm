package insyncwithfoo.ryecharm.configurations.uv

import insyncwithfoo.ryecharm.Commented
import insyncwithfoo.ryecharm.Keyed
import insyncwithfoo.ryecharm.MillisecondsOrNoLimit
import insyncwithfoo.ryecharm.configurations.Copyable
import insyncwithfoo.ryecharm.configurations.DisplayableState
import insyncwithfoo.ryecharm.configurations.HasTimeouts
import insyncwithfoo.ryecharm.configurations.ProjectOverrideState
import insyncwithfoo.ryecharm.configurations.SettingName
import insyncwithfoo.ryecharm.message


internal class UVConfigurations : DisplayableState(), HasTimeouts, Copyable {
    var executable by string(null)
    var configurationFile by string(null)
    
    var packageManaging by property(false)
    
    override var timeouts by map<SettingName, MillisecondsOrNoLimit>()
}


internal enum class UVTimeouts(override val key: String, override val comment: String) : Keyed, Commented {
    RUN("run", message("configurations.timeouts.uv.run")),
    INIT("init", message("configurations.timeouts.uv.init")),
    ADD("add", message("configurations.timeouts.uv.add")),
    REMOVE("remove", message("configurations.timeouts.uv.remove")),
    SYNC("sync", message("configurations.timeouts.uv.sync")),
    LOCK("lock", message("configurations.timeouts.uv.lock")),
    TREE("tree", message("configurations.timeouts.uv.tree")),
    TOOL_RUN("tool run", message("configurations.timeouts.uv.toolrun")),
    TOOL_INSTALL("tool install", message("configurations.timeouts.uv.toolinstall")),
    TOOL_UPGRADE("tool upgrade", message("configurations.timeouts.uv.toolupgrade")),
    TOOL_LIST("tool list", message("configurations.timeouts.uv.toollist")),
    TOOL_UNINSTALL("tool uninstall", message("configurations.timeouts.uv.tooluninstall")),
    TOOL_UPDATE_SHELL("tool update-shell", message("configurations.timeouts.uv.toolupdateshell")),
    TOOL_DIR("tool dir", message("configurations.timeouts.uv.tooldir")),
    PYTHON_LIST("python list", message("configurations.timeouts.uv.pythonlist")),
    PYTHON_INSTALL("python install", message("configurations.timeouts.uv.pythoninstall")),
    PYTHON_FIND("python find", message("configurations.timeouts.uv.pythonfind")),
    PYTHON_PIN("python pin", message("configurations.timeouts.uv.pythonpin")),
    PYTHON_DIR("python dir", message("configurations.timeouts.uv.pythondir")),
    PYTHON_UNINSTALL("python uninstall", message("configurations.timeouts.uv.pythonuninstall")),
    PIP_COMPILE("pip compile", message("configurations.timeouts.uv.pipcompile")),
    PIP_SYNC("pip sync", message("configurations.timeouts.uv.pipsync")),
    PIP_INSTALL("pip install", message("configurations.timeouts.uv.pipinstall")),
    PIP_UNINSTALL("pip uninstall", message("configurations.timeouts.uv.pipuninstall")),
    PIP_FREEZE("pip freeze", message("configurations.timeouts.uv.pipfreeze")),
    PIP_LIST("pip list", message("configurations.timeouts.uv.piplist")),
    PIP_SHOW("pip show", message("configurations.timeouts.uv.pipshow")),
    PIP_TREE("pip tree", message("configurations.timeouts.uv.piptree")),
    PIP_CHECK("pip check", message("configurations.timeouts.uv.pipcheck")),
    VENV("venv", message("configurations.timeouts.uv.venv")),
    CACHE("cache", message("configurations.timeouts.uv.cache")),
    VERSION("version", message("configurations.timeouts.uv.version"));
    
    override val label by ::key
}


internal class UVOverrides : DisplayableState(), ProjectOverrideState {
    override var list by list<SettingName>()
}
