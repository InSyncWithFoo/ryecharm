package insyncwithfoo.ryecharm

import com.intellij.util.system.OS


internal val osIsWindows by lazy {
    OS.CURRENT == OS.Windows
}
