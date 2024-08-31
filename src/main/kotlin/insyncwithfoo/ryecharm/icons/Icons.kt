package insyncwithfoo.ryecharm.icons

import com.intellij.openapi.util.IconLoader


private interface IconHolder {
    fun loadIcon(path: String) =
        IconLoader.getIcon(path, this::class.java)
}


@Suppress("unused")
internal object RyeIcons : IconHolder {
    val BIG_330 by lazy { loadIcon("icons/rye-330.svg") }
    val MEDIUM_100 by lazy { loadIcon("icons/rye-100.svg") }
    val SMALL_32 by lazy { loadIcon("icons/rye-32.svg") }
    val SMALL_28 by lazy { loadIcon("icons/rye-28.svg") }
    val TINY_18 by lazy { loadIcon("icons/rye-18.svg") }
    val TINY_16 by lazy { loadIcon("icons/rye-16.svg") }
}


internal object RuffIcons : IconHolder {
    val TINY_16 by lazy { loadIcon("icons/ruff-16.svg") }
    val TINY_16_WHITE by lazy { loadIcon("icons/ruff-16-white.svg") }
}


internal object UVIcons : IconHolder {
    val TINY_18 by lazy { loadIcon("icons/uv-18.svg") }
    val TINY_16 by lazy { loadIcon("icons/uv-16.svg") }
    val TINY_16_WHITE by lazy { loadIcon("icons/uv-16-white.svg") }
}
