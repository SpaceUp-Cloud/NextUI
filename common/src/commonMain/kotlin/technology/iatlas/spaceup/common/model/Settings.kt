package technology.iatlas.spaceup.common.model

data class Settings(
    val isAutoMode: Boolean,
    val isAutoLogin: Boolean,
    val rememberCredentials: Boolean,

    // Domains
    val isCached: Boolean
)
