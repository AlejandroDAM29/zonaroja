package alejandro.developer.core.runtime

enum class AppDataMode {
    BACKEND,
    MODS;

    val usesModsData: Boolean
        get() = this == MODS
}
