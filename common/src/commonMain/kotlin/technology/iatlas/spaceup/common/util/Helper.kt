package technology.iatlas.spaceup.common.util

object Helper {
    fun getSystemProfile(): String = System.getProperty("profile") ?: ""
}

